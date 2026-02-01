package org.ikigaidigital.integration;

import org.ikigaidigital.infrastructure.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.infrastructure.persistence.repository.JpaTimeDepositRepository;
import org.ikigaidigital.util.DatabaseTestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static org.ikigaidigital.util.TestAssertions.*;
import static org.ikigaidigital.util.TestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for Time Deposit REST endpoints.
 * Tests the complete flow from HTTP request to database operations.
 */
@DisplayName("Time Deposit Integration Tests")
class TimeDepositIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JpaTimeDepositRepository timeDepositRepository;

    @Test
    @DisplayName("Should update balances with correct interest rates for all plan types")
    void shouldUpdateBalancesSuccessfully() throws Exception {
        // Given: Create standard test deposits
        List<TimeDepositEntity> deposits = databaseTestUtil.createStandardTestDeposits();

        // When: Call update balances endpoint
        ResultActions result = mockMvc.perform(post(ApiEndpoints.UPDATE_BALANCES));

        // Then: Assert successful response
        assertUpdateBalancesSuccess(result, deposits.size());

        // And: Verify balances were updated with correct interest
        TimeDepositEntity updatedBasic = timeDepositRepository.findById(deposits.get(0).getId()).orElseThrow();
        TimeDepositEntity updatedStudent = timeDepositRepository.findById(deposits.get(1).getId()).orElseThrow();
        TimeDepositEntity updatedPremium = timeDepositRepository.findById(deposits.get(2).getId()).orElseThrow();

        assertBasicPlanInterest(updatedBasic);
        assertStudentPlanInterest(updatedStudent);
        assertPremiumPlanInterest(updatedPremium);
    }

    @Test
    @DisplayName("Should not apply interest to deposits under 30 days")
    void shouldNotUpdateBalancesForDepositsUnder30Days() throws Exception {
        // Given: Create deposit with less than minimum days
        BigDecimal originalBalance = SampleBalances.BASIC_BALANCE;
        TimeDepositEntity deposit = databaseTestUtil.createAndSaveTimeDeposit(
                PlanTypes.BASIC,
                originalBalance,
                DayThresholds.BASIC_MIN_DAYS - 5  // 25 days
        );

        // When: Call update balances endpoint
        ResultActions result = mockMvc.perform(post(ApiEndpoints.UPDATE_BALANCES));

        // Then: Assert successful response
        assertUpdateBalancesSuccess(result, 1);

        // And: Verify balance remains unchanged
        TimeDepositEntity updated = timeDepositRepository.findById(deposit.getId()).orElseThrow();
        assertBalanceUnchanged(updated, originalBalance);
    }

    @Test
    @DisplayName("Should retrieve all time deposits with their associated withdrawals")
    void shouldRetrieveAllTimeDepositsWithWithdrawals() throws Exception {
        // Given: Create test data with withdrawals
        DatabaseTestUtil.TestDataWithWithdrawals testData = databaseTestUtil.createDepositsWithWithdrawals();

        // When: Get all deposits
        ResultActions result = mockMvc.perform(get(ApiEndpoints.GET_ALL_DEPOSITS));

        // Then: Assert response contains deposits with withdrawals
        assertResponseSize(result, 2)
                .andExpect(jsonPath(JsonPaths.TOTAL_ELEMENTS).value(2))
                .andExpect(jsonPath(JsonPaths.TOTAL_PAGES).value(1));

        // And: Verify first deposit details
        assertTimeDepositAt(result, 0)
                .hasId(testData.getFirstDeposit().getId())
                .hasPlanType(PlanTypes.BASIC)
                .hasBalance(SampleBalances.BASIC_BALANCE.doubleValue())
                .hasWithdrawalCount(2);

        // And: Verify second deposit details
        assertTimeDepositAt(result, 1)
                .hasId(testData.getDepositAt(1).getId())
                .hasPlanType(PlanTypes.STUDENT)
                .hasBalance(SampleBalances.STUDENT_BALANCE.doubleValue())
                .hasWithdrawalCount(1);
    }

    @Test
    @DisplayName("Should handle pagination correctly for large datasets")
    void shouldHandlePaginationCorrectly() throws Exception {
        // Given: Create multiple deposits for pagination testing
        int totalDeposits = 25;
        databaseTestUtil.createMultipleDeposits(totalDeposits, PlanTypes.BASIC);

        // When: Request first page
        ResultActions firstPage = mockMvc.perform(get(ApiEndpoints.GET_ALL_DEPOSITS)
                .param("page", "0")
                .param("size", String.valueOf(Pagination.TEST_PAGE_SIZE)));

        // Then: Assert first page metadata
        assertResponseSize(firstPage, Pagination.TEST_PAGE_SIZE);
        assertPaginationMetadata(firstPage, totalDeposits, 3, 0);

        // When: Request last page
        ResultActions lastPage = mockMvc.perform(get(ApiEndpoints.GET_ALL_DEPOSITS)
                .param("page", "2")
                .param("size", String.valueOf(Pagination.TEST_PAGE_SIZE)));

        // Then: Assert last page has remaining items
        assertResponseSize(lastPage, 5)
                .andExpect(jsonPath(JsonPaths.PAGE_NUMBER).value(2));
    }

    @Test
    @DisplayName("Should handle sorting by balance in descending order")
    void shouldHandleSortingCorrectly() throws Exception {
        // Given: Create deposits with different balances for sorting
        databaseTestUtil.createAndSaveTimeDeposit(PlanTypes.BASIC, SampleBalances.SMALL_BALANCE, 45);
        databaseTestUtil.createAndSaveTimeDeposit(PlanTypes.STUDENT, SampleBalances.STUDENT_BALANCE, 180);
        databaseTestUtil.createAndSaveTimeDeposit(PlanTypes.PREMIUM, new BigDecimal("3000.00"), 60);

        // When: Request sorted by balance descending
        ResultActions result = mockMvc.perform(get(ApiEndpoints.GET_ALL_DEPOSITS)
                .param("sort", "balance,desc"));

        // Then: Assert deposits are sorted correctly
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].balance").value(5000.00))
                .andExpect(jsonPath("$.content[1].balance").value(3000.00))
                .andExpect(jsonPath("$.content[2].balance").value(1000.00));
    }

    @Test
    @DisplayName("Should return empty list when no deposits exist")
    void shouldReturnEmptyListWhenNoDeposits() throws Exception {
        // When: Request all deposits from empty database
        ResultActions result = mockMvc.perform(get(ApiEndpoints.GET_ALL_DEPOSITS));

        // Then: Assert empty response
        assertEmptyResponse(result);
    }
}