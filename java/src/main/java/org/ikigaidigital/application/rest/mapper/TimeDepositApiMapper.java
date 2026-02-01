package org.ikigaidigital.application.rest.mapper;

import org.ikigaidigital.api.generated.model.TimeDeposit;
import org.ikigaidigital.api.generated.model.TimeDepositsResponse;
import org.ikigaidigital.api.generated.model.UpdateBalancesResponse;
import org.ikigaidigital.api.generated.model.Withdrawal;
import org.ikigaidigital.domain.port.in.GetTimeDepositsUseCase.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.port.in.UpdateBalancesUseCase.UpdateBalancesResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TimeDepositApiMapper {

    // Mapping to generated API models
    @Mapping(target = "planType", expression = "java(mapPlanType(domain.planType()))")
    TimeDeposit toGeneratedTimeDeposit(TimeDepositWithWithdrawals domain);

    Withdrawal toGeneratedWithdrawal(org.ikigaidigital.domain.model.Withdrawal domain);

    @Mapping(target = "timestamp", expression = "java(mapTimestamp(result.timestamp()))")
    UpdateBalancesResponse toGeneratedResponse(UpdateBalancesResult result);

    default TimeDeposit.PlanTypeEnum mapPlanType(String planType) {
        if (planType == null) return null;
        return switch (planType.toLowerCase()) {
            case "basic" -> TimeDeposit.PlanTypeEnum.BASIC;
            case "student" -> TimeDeposit.PlanTypeEnum.STUDENT;
            case "premium" -> TimeDeposit.PlanTypeEnum.PREMIUM;
            default -> throw new IllegalArgumentException("Unknown plan type: " + planType);
        };
    }

    default OffsetDateTime mapTimestamp(String timestamp) {
        if (timestamp == null) return null;
        try {
            // Try parsing as ISO datetime first
            return OffsetDateTime.parse(timestamp);
        } catch (Exception e) {
            // Fallback to current time if parsing fails
            return OffsetDateTime.now(ZoneOffset.UTC);
        }
    }

    default TimeDepositsResponse toGeneratedTimeDepositsResponse(Page<TimeDepositWithWithdrawals> page) {
        var content = page.getContent().stream()
                .map(this::toGeneratedTimeDeposit)
                .collect(Collectors.toList());

        var response = new TimeDepositsResponse();
        response.setContent(content);
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        return response;
    }
}