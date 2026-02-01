package org.ikigaidigital.util;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Encapsulates common test scenarios for reuse across different test classes.
 * Follows Open/Closed Principle - open for extension, closed for modification.
 */
public final class TestScenarios {

    private TestScenarios() {
        throw new AssertionError("TestScenarios should not be instantiated");
    }

    /**
     * Creates a standard set of time deposits for testing interest calculations.
     */
    public static List<TimeDeposit> createStandardDepositScenario() {
        return Arrays.asList(
                TestDataBuilder.aTimeDeposit()
                        .withId(1)
                        .asBasicPlan()
                        .withBalance(TestConstants.SampleBalances.BASIC_BALANCE.doubleValue())
                        .withDays(45)
                        .build(),
                TestDataBuilder.aTimeDeposit()
                        .withId(2)
                        .asStudentPlan()
                        .withBalance(TestConstants.SampleBalances.STUDENT_BALANCE.doubleValue())
                        .withDays(180)
                        .build(),
                TestDataBuilder.aTimeDeposit()
                        .withId(3)
                        .asPremiumPlan()
                        .withBalance(TestConstants.SampleBalances.PREMIUM_BALANCE.doubleValue())
                        .withDays(60)
                        .build()
        );
    }

    /**
     * Creates deposits that should not receive interest.
     */
    public static List<TimeDeposit> createNoInterestScenario() {
        return Arrays.asList(
                TestDataBuilder.aTimeDeposit()
                        .withId(1)
                        .asBasicPlan()
                        .withBalance(10000.00)
                        .withDays(25) // Under 30 days
                        .build(),
                TestDataBuilder.aTimeDeposit()
                        .withId(2)
                        .asStudentPlan()
                        .withBalance(5000.00)
                        .withDays(366) // Over 365 days
                        .build(),
                TestDataBuilder.aTimeDeposit()
                        .withId(3)
                        .asPremiumPlan()
                        .withBalance(20000.00)
                        .withDays(45) // Exactly 45 days (no interest)
                        .build()
        );
    }

    /**
     * Creates withdrawals for testing.
     */
    public static List<Withdrawal> createStandardWithdrawalScenario() {
        return Arrays.asList(
                new Withdrawal(101, 1, TestConstants.SampleWithdrawals.MEDIUM_WITHDRAWAL, LocalDate.now()),
                new Withdrawal(102, 1, TestConstants.SampleWithdrawals.SMALL_WITHDRAWAL, LocalDate.now().minusDays(5)),
                new Withdrawal(103, 2, TestConstants.SampleWithdrawals.SMALL_WITHDRAWAL, LocalDate.now().minusDays(10))
        );
    }

    /**
     * Creates a paginated scenario with specified number of deposits.
     */
    public static List<TimeDeposit> createPaginationScenario(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> TestDataBuilder.aTimeDeposit()
                        .withId(i)
                        .asBasicPlan()
                        .withBalance(1000.00 * i) // Varying balances
                        .withDays(45)
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Test data for sorting scenarios.
     */
    public static class SortingScenario {
        public final TimeDeposit smallBalance;
        public final TimeDeposit mediumBalance;
        public final TimeDeposit largeBalance;

        public SortingScenario() {
            this.smallBalance = TestDataBuilder.aTimeDeposit()
                    .withId(1)
                    .asBasicPlan()
                    .withBalance(1000.00)
                    .withDays(45)
                    .build();

            this.mediumBalance = TestDataBuilder.aTimeDeposit()
                    .withId(2)
                    .asStudentPlan()
                    .withBalance(5000.00)
                    .withDays(180)
                    .build();

            this.largeBalance = TestDataBuilder.aTimeDeposit()
                    .withId(3)
                    .asPremiumPlan()
                    .withBalance(20000.00)
                    .withDays(60)
                    .build();
        }

        public List<TimeDeposit> asList() {
            return Arrays.asList(smallBalance, mediumBalance, largeBalance);
        }
    }

    /**
     * Creates edge case scenarios for robust testing.
     */
    public static class EdgeCaseScenarios {

        public static TimeDeposit zeroBalanceDeposit() {
            return TestDataBuilder.aTimeDeposit()
                    .withId(1)
                    .asBasicPlan()
                    .withBalance(0.00)
                    .withDays(45)
                    .build();
        }

        public static TimeDeposit veryLargeBalanceDeposit() {
            return TestDataBuilder.aTimeDeposit()
                    .withId(1)
                    .asPremiumPlan()
                    .withBalance(999999999.99)
                    .withDays(60)
                    .build();
        }

        public static TimeDeposit exactThresholdDaysDeposit() {
            return TestDataBuilder.aTimeDeposit()
                    .withId(1)
                    .asBasicPlan()
                    .withBalance(10000.00)
                    .withDays(30) // Exactly at threshold
                    .build();
        }
    }
}