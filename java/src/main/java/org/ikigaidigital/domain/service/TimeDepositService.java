package org.ikigaidigital.domain.service;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.domain.model.Withdrawal;
import org.ikigaidigital.domain.port.in.GetTimeDepositsUseCase;
import org.ikigaidigital.domain.port.in.UpdateBalancesUseCase;
import org.ikigaidigital.domain.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.port.out.WithdrawalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeDepositService implements UpdateBalancesUseCase, GetTimeDepositsUseCase {

    private final TimeDepositRepository timeDepositRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final TimeDepositCalculator timeDepositCalculator;

    @Override
    @Transactional
    public UpdateBalancesResult updateAllBalances() {
        // Fetch all time deposits
        var timeDeposits = timeDepositRepository.findAll();

        // ASSUMPTION: Interest is calculated on-demand when this endpoint is called
        // ASSUMPTION: Interest can be applied multiple times (no tracking of previous applications)
        // ASSUMPTION: Using existing TimeDepositCalculator logic which has plan-specific rules:
        //   - Basic: interest after 30 days
        //   - Student: interest only within 365 days
        //   - Premium: interest after 45 days
        timeDepositCalculator.updateBalance(timeDeposits);

        // Save the updated time deposits
        // ASSUMPTION: All updates succeed or fail together (transactional boundary)
        timeDepositRepository.saveAll(timeDeposits);

        // Create result
        // ASSUMPTION: Response includes count and timestamp (not specified in kata)
        var timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
        var message = String.format("Successfully updated balances for %d time deposits", timeDeposits.size());

        return new UpdateBalancesResult(message, timeDeposits.size(), timestamp);
    }

    @Override
    public Page<TimeDepositWithWithdrawals> getAllTimeDeposits(Pageable pageable) {
        // Get paginated time deposits
        // ASSUMPTION: Default pagination if not specified (handled by Spring)
        var timeDepositsPage = timeDepositRepository.findAll(pageable);

        // Get all time deposit IDs for this page
        var timeDepositIds = timeDepositsPage.getContent().stream()
                .map(TimeDeposit::getId)
                .collect(Collectors.toList());

        // Fetch all withdrawals for these time deposits in one query
        // ASSUMPTION: Withdrawals are always included in the response (not optional)
        var withdrawals = withdrawalRepository.findByTimeDepositIdIn(timeDepositIds);

        // Group withdrawals by time deposit ID
        var withdrawalsByTimeDepositId = withdrawals.stream()
                .collect(Collectors.groupingBy(Withdrawal::getTimeDepositId));

        // Map to response DTOs
        // ASSUMPTION: Empty withdrawal list if no withdrawals exist for a deposit
        var content = timeDepositsPage.getContent().stream()
                .map(td -> TimeDepositWithWithdrawals.from(
                        td,
                        withdrawalsByTimeDepositId.getOrDefault(td.getId(), List.of())
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, timeDepositsPage.getTotalElements());
    }
}