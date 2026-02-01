package org.ikigaidigital.util;

import org.ikigaidigital.infrastructure.persistence.entity.TimeDepositEntity;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Custom assertion helper for test readability.
 * Groups common assertions together following SRP.
 */
public final class TestAssertions {

    private TestAssertions() {
        throw new AssertionError("TestAssertions should not be instantiated");
    }

    /**
     * Assert standard success response for update balances endpoint.
     */
    public static ResultActions assertUpdateBalancesSuccess(ResultActions actions, int expectedCount) throws Exception {
        return actions
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestConstants.JsonPaths.MESSAGE)
                        .value(String.format("Successfully updated balances for %d time deposits", expectedCount)))
                .andExpect(jsonPath(TestConstants.JsonPaths.UPDATED_COUNT).value(expectedCount))
                .andExpect(jsonPath(TestConstants.JsonPaths.TIMESTAMP).exists());
    }

    /**
     * Assert pagination metadata in response.
     */
    public static ResultActions assertPaginationMetadata(ResultActions actions,
                                                         int expectedElements,
                                                         int expectedPages,
                                                         int currentPage) throws Exception {
        return actions
                .andExpect(jsonPath(TestConstants.JsonPaths.TOTAL_ELEMENTS).value(expectedElements))
                .andExpect(jsonPath(TestConstants.JsonPaths.TOTAL_PAGES).value(expectedPages))
                .andExpect(jsonPath(TestConstants.JsonPaths.PAGE_NUMBER).value(currentPage));
    }

    /**
     * Assert time deposit entity has expected balance after interest calculation.
     */
    public static void assertBalanceAfterInterest(TimeDepositEntity entity,
                                                  BigDecimal expectedBalance) {
        assertThat(entity.getBalance())
                .as("Balance after interest calculation")
                .isEqualByComparingTo(expectedBalance);
    }

    /**
     * Assert basic plan interest calculation.
     */
    public static void assertBasicPlanInterest(TimeDepositEntity entity) {
        assertBalanceAfterInterest(entity, TestConstants.ExpectedInterest.BASIC_BALANCE_AFTER_INTEREST);
    }

    /**
     * Assert student plan interest calculation.
     */
    public static void assertStudentPlanInterest(TimeDepositEntity entity) {
        assertBalanceAfterInterest(entity, TestConstants.ExpectedInterest.STUDENT_BALANCE_AFTER_INTEREST);
    }

    /**
     * Assert premium plan interest calculation.
     */
    public static void assertPremiumPlanInterest(TimeDepositEntity entity) {
        assertBalanceAfterInterest(entity, TestConstants.ExpectedInterest.PREMIUM_BALANCE_AFTER_INTEREST);
    }

    /**
     * Assert balance remains unchanged (no interest applied).
     */
    public static void assertBalanceUnchanged(TimeDepositEntity entity, BigDecimal originalBalance) {
        assertThat(entity.getBalance())
                .as("Balance should remain unchanged")
                .isEqualByComparingTo(originalBalance);
    }

    /**
     * Builder for fluent time deposit assertions in responses.
     */
    public static class TimeDepositResponseAssert {
        private final ResultActions actions;
        private final int index;
        private final String basePath;

        public TimeDepositResponseAssert(ResultActions actions, int index) {
            this.actions = actions;
            this.index = index;
            this.basePath = String.format("$.content[%d]", index);
        }

        public TimeDepositResponseAssert hasId(Integer expectedId) throws Exception {
            actions.andExpect(jsonPath(basePath + ".id").value(expectedId));
            return this;
        }

        public TimeDepositResponseAssert hasPlanType(String expectedPlanType) throws Exception {
            actions.andExpect(jsonPath(basePath + ".planType").value(expectedPlanType));
            return this;
        }

        public TimeDepositResponseAssert hasBalance(double expectedBalance) throws Exception {
            actions.andExpect(jsonPath(basePath + ".balance").value(expectedBalance));
            return this;
        }

        public TimeDepositResponseAssert hasDays(int expectedDays) throws Exception {
            actions.andExpect(jsonPath(basePath + ".days").value(expectedDays));
            return this;
        }

        public TimeDepositResponseAssert hasWithdrawalCount(int expectedCount) throws Exception {
            actions.andExpect(jsonPath(basePath + ".withdrawals", hasSize(expectedCount)));
            return this;
        }

        public TimeDepositResponseAssert hasNoWithdrawals() throws Exception {
            actions.andExpect(jsonPath(basePath + ".withdrawals", hasSize(0)));
            return this;
        }

        public ResultActions and() {
            return actions;
        }
    }

    /**
     * Create assertion builder for time deposit at specific index in response.
     */
    public static TimeDepositResponseAssert assertTimeDepositAt(ResultActions actions, int index) {
        return new TimeDepositResponseAssert(actions, index);
    }

    /**
     * Assert empty response list.
     */
    public static ResultActions assertEmptyResponse(ResultActions actions) throws Exception {
        return actions
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestConstants.JsonPaths.CONTENT, hasSize(0)))
                .andExpect(jsonPath(TestConstants.JsonPaths.TOTAL_ELEMENTS).value(0));
    }

    /**
     * Assert response has expected number of elements.
     */
    public static ResultActions assertResponseSize(ResultActions actions, int expectedSize) throws Exception {
        return actions
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestConstants.JsonPaths.CONTENT, hasSize(expectedSize)));
    }
}