package org.ikigaidigital.application.rest.mapper;

import org.ikigaidigital.application.rest.dto.TimeDepositResponse;
import org.ikigaidigital.application.rest.dto.TimeDepositsPageResponse;
import org.ikigaidigital.application.rest.dto.UpdateBalancesResponse;
import org.ikigaidigital.application.rest.dto.WithdrawalResponse;
import org.ikigaidigital.domain.model.Withdrawal;
import org.ikigaidigital.domain.port.in.GetTimeDepositsUseCase.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.port.in.UpdateBalancesUseCase.UpdateBalancesResult;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TimeDepositApiMapper {

    TimeDepositResponse toResponse(TimeDepositWithWithdrawals domain);

    WithdrawalResponse toResponse(Withdrawal domain);

    UpdateBalancesResponse toResponse(UpdateBalancesResult result);

    default TimeDepositsPageResponse toPageResponse(Page<TimeDepositWithWithdrawals> page) {
        var content = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return TimeDepositsPageResponse.builder()
                .content(content)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .build();
    }
}