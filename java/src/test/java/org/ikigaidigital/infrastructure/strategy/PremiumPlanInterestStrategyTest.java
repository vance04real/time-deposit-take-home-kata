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
@DisplayName("Premium Plan Interest Strategy Tests")
class PremiumPlanInterestStrategyTest {

    @Mock
    private TimeDepositProperties properties;

    private PremiumPlanInterestStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new PremiumPlanInterestStrategy(properties);
    }

    @Test
    @DisplayName("Should calculate interest within valid period")
    void shouldCalculateInterestWithinValidPeriod() {
        // Given
        var premiumConfig = new PlanConfiguration();
        premiumConfig.setAnnualRate(0.05);
        premiumConfig.setMinDays(45);
        when(properties.getPlanConfig("premium")).thenReturn(premiumConfig);

        var timeDeposit = aTimeDeposit()
                .withPlanType(PlanTypes.PREMIUM)
                .withBalance(20000.0)
                .withDays(60)
                .build();

        // When
        var interest = strategy.calculateInterest(timeDeposit);

        // Then
        assertThat(interest).isEqualTo(83.33, offset(0.01)); // 20000 * 0.05 / 12
    }

    @Test
    @DisplayName("Should return zero interest before minimum days")
    void shouldReturnZeroBeforeMinimumDays() {
        // Given
        var premiumConfig = new PlanConfiguration();
        premiumConfig.setAnnualRate(0.05);
        premiumConfig.setMinDays(45);
        when(properties.getPlanConfig("premium")).thenReturn(premiumConfig);

        var timeDeposit = aTimeDeposit()
                .withPlanType(PlanTypes.PREMIUM)
                .withBalance(20000.0)
                .withDays(44)
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
        var premiumConfig = new PlanConfiguration();
        premiumConfig.setAnnualRate(0.05);
        premiumConfig.setMinDays(45);
        when(properties.getPlanConfig("premium")).thenReturn(premiumConfig);

        // When/Then - at exactly 45 days (minimum)
        var timeDeposit45Days = aTimeDeposit()
                .withPlanType(PlanTypes.PREMIUM)
                .withBalance(20000.0)
                .withDays(45)
                .build();
        assertThat(strategy.calculateInterest(timeDeposit45Days)).isEqualTo(0.0);

        // When/Then - at 46 days (just after minimum)
        var timeDeposit46Days = aTimeDeposit()
                .withPlanType(PlanTypes.PREMIUM)
                .withBalance(20000.0)
                .withDays(46)
                .build();
        assertThat(strategy.calculateInterest(timeDeposit46Days)).isEqualTo(83.33, offset(0.01));
    }

    @Test
    @DisplayName("Should support premium plan type")
    void shouldSupportPremiumPlanType() {
        assertThat(strategy.supports("premium")).isTrue();
        assertThat(strategy.supports("PREMIUM")).isTrue();
        assertThat(strategy.supports("Premium")).isTrue();
    }

    @Test
    @DisplayName("Should not support other plan types")
    void shouldNotSupportOtherPlanTypes() {
        assertThat(strategy.supports("basic")).isFalse();
        assertThat(strategy.supports("student")).isFalse();
    }
}