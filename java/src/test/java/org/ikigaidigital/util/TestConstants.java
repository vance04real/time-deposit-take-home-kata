package org.ikigaidigital.util;

import org.assertj.core.data.Offset;

import java.math.BigDecimal;

/**
 * Constants used across test classes to ensure consistency and avoid magic numbers.
 * Groups related constants together for better organization.
 */
public final class TestConstants {

    // Private constructor to prevent instantiation
    private TestConstants() {
        throw new AssertionError("TestConstants should not be instantiated");
    }

    /**
     * Helper method to create offset for double comparisons
     */
    public static Offset<Double> offset(double value) {
        return Offset.offset(value);
    }

    /**
     * Plan types
     */
    public static final class PlanTypes {
        public static final String BASIC = "basic";
        public static final String STUDENT = "student";
        public static final String PREMIUM = "premium";

        private PlanTypes() {
        }
    }

    /**
     * Interest rates
     */
    public static final class InterestRates {
        public static final double BASIC_RATE = 0.01;      // 1% annual
        public static final double STUDENT_RATE = 0.03;    // 3% annual
        public static final double PREMIUM_RATE = 0.05;    // 5% annual
        public static final int MONTHS_PER_YEAR = 12;

        private InterestRates() {
        }
    }

    /**
     * Day thresholds for interest calculation
     */
    public static final class DayThresholds {
        public static final int BASIC_MIN_DAYS = 30;
        public static final int PREMIUM_MIN_DAYS = 45;
        public static final int STUDENT_MAX_DAYS = 365;

        private DayThresholds() {
        }
    }

    /**
     * Sample balances for testing
     */
    public static final class SampleBalances {
        public static final BigDecimal BASIC_BALANCE = new BigDecimal("10000.00");
        public static final BigDecimal STUDENT_BALANCE = new BigDecimal("5000.00");
        public static final BigDecimal PREMIUM_BALANCE = new BigDecimal("20000.00");
        public static final BigDecimal LARGE_BALANCE = new BigDecimal("50000.00");
        public static final BigDecimal SMALL_BALANCE = new BigDecimal("1000.00");

        private SampleBalances() {
        }
    }

    /**
     * Sample withdrawal amounts
     */
    public static final class SampleWithdrawals {
        public static final BigDecimal SMALL_WITHDRAWAL = new BigDecimal("100.00");
        public static final BigDecimal MEDIUM_WITHDRAWAL = new BigDecimal("500.00");
        public static final BigDecimal LARGE_WITHDRAWAL = new BigDecimal("1000.00");

        private SampleWithdrawals() {
        }
    }

    /**
     * Expected interest calculations
     */
    public static final class ExpectedInterest {
        // Basic plan: 10000 * 0.01 / 12 = 8.33
        public static final BigDecimal BASIC_MONTHLY_INTEREST = new BigDecimal("8.33");
        public static final BigDecimal BASIC_BALANCE_AFTER_INTEREST = new BigDecimal("10008.33");

        // Student plan: 5000 * 0.03 / 12 = 12.50
        public static final BigDecimal STUDENT_MONTHLY_INTEREST = new BigDecimal("12.50");
        public static final BigDecimal STUDENT_BALANCE_AFTER_INTEREST = new BigDecimal("5012.50");

        // Premium plan: 20000 * 0.05 / 12 = 83.33
        public static final BigDecimal PREMIUM_MONTHLY_INTEREST = new BigDecimal("83.33");
        public static final BigDecimal PREMIUM_BALANCE_AFTER_INTEREST = new BigDecimal("20083.33");

        private ExpectedInterest() {
        }
    }

    /**
     * API endpoints
     */
    public static final class ApiEndpoints {
        public static final String UPDATE_BALANCES = "/api/time-deposits/update-balances";
        public static final String GET_ALL_DEPOSITS = "/api/time-deposits";

        private ApiEndpoints() {
        }
    }

    /**
     * Pagination defaults
     */
    public static final class Pagination {
        public static final int DEFAULT_PAGE_SIZE = 20;
        public static final int TEST_PAGE_SIZE = 10;
        public static final int FIRST_PAGE = 0;

        private Pagination() {
        }
    }

    /**
     * JSON paths for assertions
     */
    public static final class JsonPaths {
        public static final String CONTENT = "$.content";
        public static final String TOTAL_ELEMENTS = "$.totalElements";
        public static final String TOTAL_PAGES = "$.totalPages";
        public static final String PAGE_NUMBER = "$.pageNumber";
        public static final String PAGE_SIZE = "$.pageSize";
        public static final String MESSAGE = "$.message";
        public static final String UPDATED_COUNT = "$.updatedCount";
        public static final String TIMESTAMP = "$.timestamp";

        // Nested paths
        public static final String CONTENT_SIZE = "$.content.size()";
        public static final String FIRST_ELEMENT_ID = "$.content[0].id";
        public static final String FIRST_ELEMENT_PLAN_TYPE = "$.content[0].planType";
        public static final String FIRST_ELEMENT_BALANCE = "$.content[0].balance";
        public static final String FIRST_ELEMENT_WITHDRAWALS = "$.content[0].withdrawals";

        private JsonPaths() {
        }
    }
}