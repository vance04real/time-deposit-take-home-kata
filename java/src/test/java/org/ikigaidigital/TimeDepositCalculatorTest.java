package org.ikigaidigital;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class TimeDepositCalculatorTest {

    private TimeDepositCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new TimeDepositCalculator();
    }

    @Test
    void shouldApplyBasicPlanInterestAfter30Days() {
        // Given: Basic plan with balance 12000 and 45 days
        TimeDeposit basicPlan = new TimeDeposit(1, "basic", 12000.00, 45);
        List<TimeDeposit> plans = Arrays.asList(basicPlan);

        // When: Update balance
        calculator.updateBalance(plans);

        // Then: 1% annual interest applied monthly = 12000 * 0.01 / 12 = 10.00
        assertThat(basicPlan.getBalance()).isEqualTo(12010.00);
    }

    @Test
    void shouldNotApplyInterestForFirst30Days() {
        // Given: Plans with less than 30 days
        TimeDeposit basic = new TimeDeposit(1, "basic", 10000.00, 30);
        TimeDeposit student = new TimeDeposit(2, "student", 5000.00, 25);
        TimeDeposit premium = new TimeDeposit(3, "premium", 20000.00, 20);
        List<TimeDeposit> plans = Arrays.asList(basic, student, premium);

        // When: Update balance
        calculator.updateBalance(plans);

        // Then: No interest applied
        assertThat(basic.getBalance()).isEqualTo(10000.00);
        assertThat(student.getBalance()).isEqualTo(5000.00);
        assertThat(premium.getBalance()).isEqualTo(20000.00);
    }

    @Test
    void shouldApplyStudentPlanInterestWithin365Days() {
        // Given: Student plan within 365 days
        TimeDeposit studentPlan = new TimeDeposit(1, "student", 6000.00, 180);
        List<TimeDeposit> plans = Arrays.asList(studentPlan);

        // When: Update balance
        calculator.updateBalance(plans);

        // Then: 3% annual interest applied monthly = 6000 * 0.03 / 12 = 15.00
        assertThat(studentPlan.getBalance()).isEqualTo(6015.00);
    }

    @Test
    void shouldNotApplyStudentPlanInterestAfter365Days() {
        // Given: Student plan after 365 days
        TimeDeposit studentPlan = new TimeDeposit(1, "student", 8000.00, 366);
        List<TimeDeposit> plans = Arrays.asList(studentPlan);

        // When: Update balance
        calculator.updateBalance(plans);

        // Then: No interest applied
        assertThat(studentPlan.getBalance()).isEqualTo(8000.00);
    }

    @Test
    void shouldApplyPremiumPlanInterestAfter45Days() {
        // Given: Premium plan after 45 days
        TimeDeposit premiumPlan = new TimeDeposit(1, "premium", 24000.00, 60);
        List<TimeDeposit> plans = Arrays.asList(premiumPlan);

        // When: Update balance
        calculator.updateBalance(plans);

        // Then: 5% annual interest applied monthly = 24000 * 0.05 / 12 = 100.00
        assertThat(premiumPlan.getBalance()).isEqualTo(24100.00);
    }

    @Test
    void shouldNotApplyPremiumPlanInterestBefore46Days() {
        // Given: Premium plan at exactly 45 days
        TimeDeposit premiumPlan = new TimeDeposit(1, "premium", 30000.00, 45);
        List<TimeDeposit> plans = Arrays.asList(premiumPlan);

        // When: Update balance
        calculator.updateBalance(plans);

        // Then: No interest applied (interest starts after 45 days)
        assertThat(premiumPlan.getBalance()).isEqualTo(30000.00);
    }

    @Test
    void shouldRoundInterestToTwoDecimalPlaces() {
        // Given: Balance that results in fractional interest
        TimeDeposit basicPlan = new TimeDeposit(1, "basic", 12345.67, 45);
        List<TimeDeposit> plans = Arrays.asList(basicPlan);

        // When: Update balance
        calculator.updateBalance(plans);

        // Then: Interest = 12345.67 * 0.01 / 12 = 10.2880583... rounded to 10.29
        assertThat(basicPlan.getBalance()).isCloseTo(12355.96, within(0.01));
    }

    @Test
    void shouldProcessMultiplePlans() {
        // Given: Multiple plans
        TimeDeposit basic = new TimeDeposit(1, "basic", 12000.00, 45);
        TimeDeposit student = new TimeDeposit(2, "student", 6000.00, 180);
        TimeDeposit premium = new TimeDeposit(3, "premium", 24000.00, 60);
        List<TimeDeposit> plans = Arrays.asList(basic, student, premium);

        // When: Update balance
        calculator.updateBalance(plans);

        // Then: Each plan gets appropriate interest
        assertThat(basic.getBalance()).isEqualTo(12010.00); // 12000 + 10
        assertThat(student.getBalance()).isEqualTo(6015.00); // 6000 + 15
        assertThat(premium.getBalance()).isEqualTo(24100.00); // 24000 + 100
    }

    @Test
    void shouldHandleEmptyList() {
        // Given: Empty list
        List<TimeDeposit> plans = Arrays.asList();

        // When: Update balance
        calculator.updateBalance(plans);

        // Then: No exception thrown
        assertThat(plans).isEmpty();
    }

    @Test
    void shouldHandleZeroBalance() {
        // Given: Plan with zero balance
        TimeDeposit basicPlan = new TimeDeposit(1, "basic", 0.00, 45);
        List<TimeDeposit> plans = Arrays.asList(basicPlan);

        // When: Update balance
        calculator.updateBalance(plans);

        // Then: Balance remains zero
        assertThat(basicPlan.getBalance()).isEqualTo(0.00);
    }
}
