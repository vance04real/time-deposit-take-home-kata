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
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayName("Basic Plan Interest Strategy Tests")
class BasicPlanInterestStrategyTest {

    @Mock
    private TimeDepositProperties properties;

    private BasicPlanInterestStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new BasicPlanInterestStrategy(properties);

        var basicConfig = new PlanConfiguration();
        basicConfig.setAnnualRate(0.01);
        basicConfig.setMinDays(30);

        lenient().when(properties.getPlanConfig("basic")).thenReturn(basicConfig);
    }

    @Test
    @DisplayName("Should calculate interest correctly after minimum days")
    void shouldCalculateInterestAfterMinimumDays() {
        // Given
        var timeDeposit = aTimeDeposit()
                .withPlanType(PlanTypes.BASIC)
                .withBalance(10000.0)
                .withDays(45)
                .build();

        // When
        var interest = strategy.calculateInterest(timeDeposit);

        // Then
        assertThat(interest).isEqualTo(8.33, offset(0.01)); // 10000 * 0.01 / 12
    }

    @Test
    @DisplayName("Should return zero interest before minimum days")
    void shouldReturnZeroBeforeMinimumDays() {
        // Given
        var timeDeposit = aTimeDeposit()
                .withPlanType(PlanTypes.BASIC)
                .withBalance(10000.0)
                .withDays(29)
                .build();

        // When
        var interest = strategy.calculateInterest(timeDeposit);

        // Then
        assertThat(interest).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should support basic plan type")
    void shouldSupportBasicPlanType() {
        assertThat(strategy.supports("basic")).isTrue();
        assertThat(strategy.supports("BASIC")).isTrue();
        assertThat(strategy.supports("Basic")).isTrue();
    }

    @Test
    @DisplayName("Should not support other plan types")
    void shouldNotSupportOtherPlanTypes() {
        assertThat(strategy.supports("student")).isFalse();
        assertThat(strategy.supports("premium")).isFalse();
    }
}