package org.ikigaidigital.domain.service;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.domain.model.Withdrawal;
import org.ikigaidigital.domain.port.in.GetTimeDepositsUseCase.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.port.in.UpdateBalancesUseCase.UpdateBalancesResult;
import org.ikigaidigital.domain.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.port.out.WithdrawalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ikigaidigital.util.TestConstants.SampleBalances;
import static org.ikigaidigital.util.TestDataBuilder.aTimeDeposit;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Time Deposit Service Unit Tests")
class TimeDepositServiceTest {

    @Mock
    private TimeDepositRepository timeDepositRepository;

    @Mock
    private WithdrawalRepository withdrawalRepository;

    @Mock
    private TimeDepositCalculator timeDepositCalculator;

    private TimeDepositService service;

    @BeforeEach
    void setUp() {
        service = new TimeDepositService(timeDepositRepository, withdrawalRepository, timeDepositCalculator);
    }

    @Test
    @DisplayName("Should fetch, calculate and save deposits when updating balances")
    void updateAllBalances_shouldFetchCalculateAndSaveDeposits() {
        // Given: Mock repository returns test deposits
        List<TimeDeposit> deposits = Arrays.asList(
                aTimeDeposit()
                        .withId(1)
                        .asBasicPlan()
                        .withBalance(SampleBalances.BASIC_BALANCE.doubleValue())
                        .withDays(45)
                        .build(),
                aTimeDeposit()
                        .withId(2)
                        .asStudentPlan()
                        .withBalance(SampleBalances.STUDENT_BALANCE.doubleValue())
                        .withDays(180)
                        .build()
        );
        when(timeDepositRepository.findAll()).thenReturn(deposits);

        // When: Update all balances
        UpdateBalancesResult result = service.updateAllBalances();

        // Then: Verify interactions and result
        verify(timeDepositRepository).findAll();
        verify(timeDepositCalculator).updateBalance(deposits);
        verify(timeDepositRepository).saveAll(deposits);

        assertThat(result.message()).contains("Successfully updated balances for 2 time deposits");
        assertThat(result.updatedCount()).isEqualTo(2);
        assertThat(result.timestamp()).isNotNull();
    }

    @Test
    void updateAllBalances_shouldHandleEmptyDeposits() {
        // Given
        when(timeDepositRepository.findAll()).thenReturn(List.of());

        // When
        UpdateBalancesResult result = service.updateAllBalances();

        // Then
        verify(timeDepositCalculator).updateBalance(anyList());
        assertThat(result.updatedCount()).isEqualTo(0);
        assertThat(result.message()).contains("Successfully updated balances for 0 time deposits");
    }

    @Test
    void getAllTimeDeposits_shouldReturnPagedDepositsWithWithdrawals() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<TimeDeposit> deposits = Arrays.asList(
                new TimeDeposit(1, "basic", 10000.0, 45),
                new TimeDeposit(2, "student", 5000.0, 180)
        );
        Page<TimeDeposit> depositsPage = new PageImpl<>(deposits, pageable, 2);

        List<Withdrawal> withdrawals = Arrays.asList(
                new Withdrawal(101, 1, BigDecimal.valueOf(500), LocalDate.now()),
                new Withdrawal(102, 1, BigDecimal.valueOf(200), LocalDate.now().minusDays(5)),
                new Withdrawal(103, 2, BigDecimal.valueOf(100), LocalDate.now().minusDays(10))
        );

        when(timeDepositRepository.findAll(pageable)).thenReturn(depositsPage);
        when(withdrawalRepository.findByTimeDepositIdIn(Arrays.asList(1, 2))).thenReturn(withdrawals);

        // When
        Page<TimeDepositWithWithdrawals> result = service.getAllTimeDeposits(pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        // Check first deposit has 2 withdrawals
        TimeDepositWithWithdrawals first = result.getContent().get(0);
        assertThat(first.id()).isEqualTo(1);
        assertThat(first.withdrawals()).hasSize(2);

        // Check second deposit has 1 withdrawal
        TimeDepositWithWithdrawals second = result.getContent().get(1);
        assertThat(second.id()).isEqualTo(2);
        assertThat(second.withdrawals()).hasSize(1);
    }

    @Test
    void getAllTimeDeposits_shouldHandleDepositsWithoutWithdrawals() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<TimeDeposit> deposits = Arrays.asList(
                new TimeDeposit(1, "basic", 10000.0, 45)
        );
        Page<TimeDeposit> depositsPage = new PageImpl<>(deposits, pageable, 1);

        when(timeDepositRepository.findAll(pageable)).thenReturn(depositsPage);
        when(withdrawalRepository.findByTimeDepositIdIn(anyList())).thenReturn(List.of());

        // When
        Page<TimeDepositWithWithdrawals> result = service.getAllTimeDeposits(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).withdrawals()).isEmpty();
    }

    @Test
    void updateAllBalances_shouldUseCalculatorCorrectly() {
        // Given
        List<TimeDeposit> deposits = Arrays.asList(
                new TimeDeposit(1, "basic", 10000.0, 45)
        );
        when(timeDepositRepository.findAll()).thenReturn(deposits);

        // When
        service.updateAllBalances();

        // Then
        ArgumentCaptor<List<TimeDeposit>> captor = ArgumentCaptor.forClass(List.class);
        verify(timeDepositCalculator).updateBalance(captor.capture());
        assertThat(captor.getValue()).isEqualTo(deposits);
    }
}