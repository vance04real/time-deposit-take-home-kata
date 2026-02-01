package org.ikigaidigital.application.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikigaidigital.api.generated.model.TimeDeposit;
import org.ikigaidigital.api.generated.model.TimeDepositsResponse;
import org.ikigaidigital.api.generated.model.UpdateBalancesResponse;
import org.ikigaidigital.application.rest.mapper.TimeDepositApiMapper;
import org.ikigaidigital.domain.model.Withdrawal;
import org.ikigaidigital.domain.port.in.GetTimeDepositsUseCase;
import org.ikigaidigital.domain.port.in.GetTimeDepositsUseCase.TimeDepositWithWithdrawals;
import org.ikigaidigital.domain.port.in.UpdateBalancesUseCase;
import org.ikigaidigital.domain.port.in.UpdateBalancesUseCase.UpdateBalancesResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TimeDepositController.class)
@ContextConfiguration(classes = {TimeDepositController.class, TimeDepositApiMapperImpl.class})
class TimeDepositControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UpdateBalancesUseCase updateBalancesUseCase;

    @MockBean
    private GetTimeDepositsUseCase getTimeDepositsUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void updateBalances_shouldReturnSuccessResponse() throws Exception {
        // Given
        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
        UpdateBalancesResult result = new UpdateBalancesResult(
                "Successfully updated balances for 5 time deposits",
                5,
                timestamp
        );
        when(updateBalancesUseCase.updateAllBalances()).thenReturn(result);

        // When & Then
        mockMvc.perform(post("/api/time-deposits/update-balances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully updated balances for 5 time deposits"))
                .andExpect(jsonPath("$.updatedCount").value(5))
                .andExpect(jsonPath("$.timestamp").value(timestamp));
    }

    @Test
    void getAllTimeDeposits_shouldReturnPagedResults() throws Exception {
        // Given
        List<Withdrawal> withdrawals1 = Arrays.asList(
                new Withdrawal(101, 1, BigDecimal.valueOf(500), LocalDate.of(2024, 1, 15)),
                new Withdrawal(102, 1, BigDecimal.valueOf(200), LocalDate.of(2024, 1, 10))
        );

        List<Withdrawal> withdrawals2 = Arrays.asList(
                new Withdrawal(103, 2, BigDecimal.valueOf(100), LocalDate.of(2024, 1, 20))
        );

        List<TimeDepositWithWithdrawals> content = Arrays.asList(
                new TimeDepositWithWithdrawals(1, "basic", 10347.56, 45, withdrawals1),
                new TimeDepositWithWithdrawals(2, "student", 5234.89, 180, withdrawals2)
        );

        Page<TimeDepositWithWithdrawals> page = new PageImpl<>(content, PageRequest.of(0, 20), 50);
        when(getTimeDepositsUseCase.getAllTimeDeposits(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].planType").value("basic"))
                .andExpect(jsonPath("$.content[0].balance").value(10347.56))
                .andExpect(jsonPath("$.content[0].days").value(45))
                .andExpect(jsonPath("$.content[0].withdrawals", hasSize(2)))
                .andExpect(jsonPath("$.content[0].withdrawals[0].amount").value(500.0))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].withdrawals", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(50))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(20));
    }

    @Test
    void getAllTimeDeposits_shouldAcceptPaginationParameters() throws Exception {
        // Given
        Page<TimeDepositWithWithdrawals> emptyPage = new PageImpl<>(
                List.of(),
                PageRequest.of(2, 10),
                0
        );
        when(getTimeDepositsUseCase.getAllTimeDeposits(any(Pageable.class))).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/time-deposits")
                        .param("page", "2")
                        .param("size", "10")
                        .param("sort", "balance,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.pageNumber").value(2))
                .andExpect(jsonPath("$.pageSize").value(10));
    }

    @Test
    void getAllTimeDeposits_shouldHandleDefaultParameters() throws Exception {
        // Given
        Page<TimeDepositWithWithdrawals> page = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 20),
                0
        );
        when(getTimeDepositsUseCase.getAllTimeDeposits(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(20));
    }
}

// MapStruct implementation stub for testing
class TimeDepositApiMapperImpl implements TimeDepositApiMapper {

    @Override
    public TimeDeposit toGeneratedTimeDeposit(TimeDepositWithWithdrawals domain) {
        if (domain == null) {
            return null;
        }

        TimeDeposit response = new TimeDeposit();
        response.setId(domain.id());
        response.setPlanType(mapPlanType(domain.planType()));
        response.setBalance(domain.balance());
        response.setDays(domain.days());
        response.setWithdrawals(domain.withdrawals().stream()
                .map(this::toGeneratedWithdrawal)
                .toList());

        return response;
    }

    @Override
    public org.ikigaidigital.api.generated.model.Withdrawal toGeneratedWithdrawal(Withdrawal domain) {
        if (domain == null) {
            return null;
        }

        org.ikigaidigital.api.generated.model.Withdrawal withdrawal = new org.ikigaidigital.api.generated.model.Withdrawal();
        withdrawal.setId(domain.getId());
        withdrawal.setAmount(domain.getAmount().doubleValue());
        withdrawal.setDate(domain.getDate());
        return withdrawal;
    }

    @Override
    public UpdateBalancesResponse toGeneratedResponse(UpdateBalancesResult result) {
        if (result == null) {
            return null;
        }

        UpdateBalancesResponse response = new UpdateBalancesResponse();
        response.setMessage(result.message());
        response.setUpdatedCount(result.updatedCount());
        response.setTimestamp(mapTimestamp(result.timestamp()));
        return response;
    }

    @Override
    public TimeDeposit.PlanTypeEnum mapPlanType(String planType) {
        if (planType == null) return null;
        return switch (planType.toLowerCase()) {
            case "basic" -> TimeDeposit.PlanTypeEnum.BASIC;
            case "student" -> TimeDeposit.PlanTypeEnum.STUDENT;
            case "premium" -> TimeDeposit.PlanTypeEnum.PREMIUM;
            default -> throw new IllegalArgumentException("Unknown plan type: " + planType);
        };
    }

    @Override
    public OffsetDateTime mapTimestamp(String timestamp) {
        if (timestamp == null) return null;
        try {
            return OffsetDateTime.parse(timestamp);
        } catch (Exception e) {
            return OffsetDateTime.now(ZoneOffset.UTC);
        }
    }

    @Override
    public TimeDepositsResponse toGeneratedTimeDepositsResponse(Page<TimeDepositWithWithdrawals> page) {
        var content = page.getContent().stream()
                .map(this::toGeneratedTimeDeposit)
                .toList();

        var response = new TimeDepositsResponse();
        response.setContent(content);
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        return response;
    }
}