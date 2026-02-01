package org.ikigaidigital.infrastructure.strategy;

import org.ikigaidigital.infrastructure.config.TimeDepositProperties;
import org.ikigaidigital.infrastructure.config.TimeDepositProperties.PlanConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.ikigaidigital.util.TestConstants.PlanTypes;
import static org.ikigaidigital.util.TestDataBuilder.aTimeDeposit;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Student Plan Interest Strategy Tests")
class StudentPlanInterestStrategyTest {

    @Mock
    private TimeDepositProperties properties;

    private StudentPlanInterestStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new StudentPlanInterestStrategy(properties);
    }

    @Test
    @DisplayName("Should calculate interest within valid period")
    void shouldCalculateInterestWithinValidPeriod() {
        // Given
        var studentConfig = new PlanConfiguration();
        studentConfig.setAnnualRate(0.03);
        studentConfig.setMinDays(30);
        studentConfig.setMaxDays(365);
        when(properties.getPlanConfig("student")).thenReturn(studentConfig);

        var timeDeposit = aTimeDeposit()
                .withPlanType(PlanTypes.STUDENT)
                .withBalance(5000.0)
                .withDays(180)
                .build();

        // When
        var interest = strategy.calculateInterest(timeDeposit);

        // Then
        assertThat(interest).isEqualTo(12.5, offset(0.01)); // 5000 * 0.03 / 12
    }

    @Test
    @DisplayName("Should return zero interest before minimum days")
    void shouldReturnZeroBeforeMinimumDays() {
        // Given
        var studentConfig = new PlanConfiguration();
        studentConfig.setAnnualRate(0.03);
        studentConfig.setMinDays(30);
        studentConfig.setMaxDays(365);
        when(properties.getPlanConfig("student")).thenReturn(studentConfig);

        var timeDeposit = aTimeDeposit()
                .withPlanType(PlanTypes.STUDENT)
                .withBalance(5000.0)
                .withDays(29)
                .build();

        // When
        var interest = strategy.calculateInterest(timeDeposit);

        // Then
        assertThat(interest).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should return zero interest after maximum days")
    void shouldReturnZeroAfterMaximumDays() {
        // Given
        var studentConfig = new PlanConfiguration();
        studentConfig.setAnnualRate(0.03);
        studentConfig.setMinDays(30);
        studentConfig.setMaxDays(365);
        when(properties.getPlanConfig("student")).thenReturn(studentConfig);

        var timeDeposit = aTimeDeposit()
                .withPlanType(PlanTypes.STUDENT)
                .withBalance(5000.0)
                .withDays(366)
                .build();

        // When
        var interest = strategy.calculateInterest(timeDeposit);

        // Then
        assertThat(interest).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should calculate interest on boundary days")
    void shouldCalculateInterestOnBoundaryDays() {
        // Given
        var studentConfig = new PlanConfiguration();
        studentConfig.setAnnualRate(0.03);
        studentConfig.setMinDays(30);
        studentConfig.setMaxDays(365);
        when(properties.getPlanConfig("student")).thenReturn(studentConfig);

        // When/Then - at exactly 30 days (minimum)
        var timeDeposit30Days = aTimeDeposit()
                .withPlanType(PlanTypes.STUDENT)
                .withBalance(5000.0)
                .withDays(30)
                .build();
        assertThat(strategy.calculateInterest(timeDeposit30Days)).isEqualTo(0.0);

        // When/Then - at 31 days (just after minimum)
        var timeDeposit31Days = aTimeDeposit()
                .withPlanType(PlanTypes.STUDENT)
                .withBalance(5000.0)
                .withDays(31)
                .build();
        assertThat(strategy.calculateInterest(timeDeposit31Days)).isEqualTo(12.5, offset(0.01));

        // When/Then - at 365 days (maximum)
        var timeDeposit365Days = aTimeDeposit()
                .withPlanType(PlanTypes.STUDENT)
                .withBalance(5000.0)
                .withDays(365)
                .build();
        assertThat(strategy.calculateInterest(timeDeposit365Days)).isEqualTo(12.5, offset(0.01));
    }

    @Test
    @DisplayName("Should support student plan type")
    void shouldSupportStudentPlanType() {
        assertThat(strategy.supports("student")).isTrue();
        assertThat(strategy.supports("STUDENT")).isTrue();
        assertThat(strategy.supports("Student")).isTrue();
    }
}