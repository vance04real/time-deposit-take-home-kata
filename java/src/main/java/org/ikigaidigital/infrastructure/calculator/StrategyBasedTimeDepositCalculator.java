package org.ikigaidigital.infrastructure.calculator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.domain.strategy.InterestCalculationStrategy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Strategy-based implementation of TimeDepositCalculator.
 * Uses different strategies for different plan types to calculate interest.
 */
@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class StrategyBasedTimeDepositCalculator extends TimeDepositCalculator {

    private final List<InterestCalculationStrategy> strategies;

    @Override
    public void updateBalance(List<TimeDeposit> timeDeposits) {
        for (TimeDeposit timeDeposit : timeDeposits) {
            var strategy = findStrategy(timeDeposit.getPlanType());
            var interest = strategy != null
                    ? strategy.calculateInterest(timeDeposit)
                    : 0.0;

            if (interest > 0) {
                var roundedInterest = BigDecimal.valueOf(interest)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();

                var newBalance = timeDeposit.getBalance() + roundedInterest;
                timeDeposit.setBalance(newBalance);

                log.debug("Applied interest of {} to time deposit {} (plan: {})",
                        roundedInterest, timeDeposit.getId(), timeDeposit.getPlanType());
            }
        }
    }

    private InterestCalculationStrategy findStrategy(String planType) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(planType))
                .findFirst()
                .orElseGet(() -> {
                    log.warn("No strategy found for plan type: {}", planType);
                    return null;
                });
    }
}