package org.ikigaidigital.infrastructure.calculator;

import org.ikigaidigital.domain.strategy.InterestCalculationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.ikigaidigital.util.TestConstants.PlanTypes;
import static org.ikigaidigital.util.TestDataBuilder.aTimeDeposit;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Strategy-Based Time Deposit Calculator Tests")
class StrategyBasedTimeDepositCalculatorTest {

    @Mock
    private InterestCalculationStrategy basicStrategy;

    @Mock
    private InterestCalculationStrategy studentStrategy;

    @Mock
    private InterestCalculationStrategy premiumStrategy;

    private StrategyBasedTimeDepositCalculator calculator;

    @BeforeEach
    void setUp() {
        var strategies = Arrays.asList(basicStrategy, studentStrategy, premiumStrategy);
        calculator = new StrategyBasedTimeDepositCalculator(strategies);

        // Configure strategy support with lenient to avoid unnecessary stubbing warnings
        lenient().when(basicStrategy.supports("basic")).thenReturn(true);
        lenient().when(studentStrategy.supports("student")).thenReturn(true);
        lenient().when(premiumStrategy.supports("premium")).thenReturn(true);
    }

    @Test
    @DisplayName("Should apply correct strategy based on plan type")
    void shouldApplyCorrectStrategyBasedOnPlanType() {
        // Given
        var basicDeposit = aTimeDeposit()
                .withPlanType(PlanTypes.BASIC)
                .withBalance(10000.0)
                .withDays(45)
                .build();

        var studentDeposit = aTimeDeposit()
                .withPlanType(PlanTypes.STUDENT)
                .withBalance(5000.0)
                .withDays(180)
                .build();

        var premiumDeposit = aTimeDeposit()
                .withPlanType(PlanTypes.PREMIUM)
                .withBalance(50000.0)
                .withDays(60)
                .build();

        var deposits = Arrays.asList(basicDeposit, studentDeposit, premiumDeposit);

        when(basicStrategy.calculateInterest(basicDeposit)).thenReturn(8.33);
        when(studentStrategy.calculateInterest(studentDeposit)).thenReturn(12.50);
        when(premiumStrategy.calculateInterest(premiumDeposit)).thenReturn(208.33);

        // When
        calculator.updateBalance(deposits);

        // Then
        assertThat(basicDeposit.getBalance()).isEqualTo(10008.33, offset(0.01));
        assertThat(studentDeposit.getBalance()).isEqualTo(5012.50, offset(0.01));
        assertThat(premiumDeposit.getBalance()).isEqualTo(50208.33, offset(0.01));

        verify(basicStrategy).calculateInterest(basicDeposit);
        verify(studentStrategy).calculateInterest(studentDeposit);
        verify(premiumStrategy).calculateInterest(premiumDeposit);
    }

    @Test
    @DisplayName("Should not update balance when interest is zero")
    void shouldNotUpdateBalanceWhenInterestIsZero() {
        // Given
        var deposit = aTimeDeposit()
                .withPlanType(PlanTypes.BASIC)
                .withBalance(10000.0)
                .withDays(20)
                .build();

        when(basicStrategy.calculateInterest(deposit)).thenReturn(0.0);

        // When
        calculator.updateBalance(Arrays.asList(deposit));

        // Then
        assertThat(deposit.getBalance()).isEqualTo(10000.0);
    }

    @Test
    @DisplayName("Should handle unknown plan type gracefully")
    void shouldHandleUnknownPlanTypeGracefully() {
        // Given
        var deposit = aTimeDeposit()
                .withPlanType("unknown")
                .withBalance(10000.0)
                .withDays(60)
                .build();

        lenient().when(basicStrategy.supports("unknown")).thenReturn(false);
        lenient().when(studentStrategy.supports("unknown")).thenReturn(false);
        lenient().when(premiumStrategy.supports("unknown")).thenReturn(false);

        var originalBalance = deposit.getBalance();

        // When
        calculator.updateBalance(Arrays.asList(deposit));

        // Then
        assertThat(deposit.getBalance()).isEqualTo(originalBalance);
    }

    @Test
    @DisplayName("Should process multiple deposits with mixed strategies")
    void shouldProcessMultipleDepositsWithMixedStrategies() {
        // Given
        var deposits = Arrays.asList(
                aTimeDeposit().withPlanType(PlanTypes.BASIC).withBalance(1000.0).withDays(45).build(),
                aTimeDeposit().withPlanType(PlanTypes.STUDENT).withBalance(2000.0).withDays(25).build(),
                aTimeDeposit().withPlanType(PlanTypes.PREMIUM).withBalance(3000.0).withDays(50).build()
        );

        when(basicStrategy.calculateInterest(any())).thenReturn(0.83);
        when(studentStrategy.calculateInterest(any())).thenReturn(0.0); // Before minimum days
        when(premiumStrategy.calculateInterest(any())).thenReturn(12.50);

        // When
        calculator.updateBalance(deposits);

        // Then
        assertThat(deposits.get(0).getBalance()).isEqualTo(1000.83, offset(0.01));
        assertThat(deposits.get(1).getBalance()).isEqualTo(2000.0); // No change
        assertThat(deposits.get(2).getBalance()).isEqualTo(3012.50, offset(0.01));
    }
}