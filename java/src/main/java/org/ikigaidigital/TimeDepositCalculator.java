package org.ikigaidigital;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.domain.strategy.InterestCalculationStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Calculator for updating time deposit balances with interest.
 * Refactored to use Strategy Pattern while maintaining exact same behavior.
 */
@RequiredArgsConstructor
public class TimeDepositCalculator {

    private final List<InterestCalculationStrategy> strategies;

    /**
     * No-arg constructor for backward compatibility
     */
    public TimeDepositCalculator() {
        this(List.of());
    }

    /**
     * Updates the balance of time deposits by applying interest.
     * Behavior remains exactly as the original implementation.
     *
     * @param timeDeposits List of time deposits to update
     */
    public void updateBalance(List<TimeDeposit> timeDeposits) {
        for (var timeDeposit : timeDeposits) {
            var interest = calculateInterest(timeDeposit);

            // Same calculation as original: balance + rounded interest
            var roundedInterest = new BigDecimal(interest)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            var newBalance = timeDeposit.getBalance() + roundedInterest;
            timeDeposit.setBalance(newBalance);
        }
    }

    private double calculateInterest(TimeDeposit timeDeposit) {
        // Use strategies if available
        if (!strategies.isEmpty()) {
            return strategies.stream()
                    .filter(strategy -> strategy.supports(timeDeposit.getPlanType()))
                    .findFirst()
                    .map(strategy -> strategy.calculateInterest(timeDeposit))
                    .orElse(0.0);
        }

        // Fallback to original logic for backward compatibility
        return calculateInterestOriginal(timeDeposit);
    }

    /**
     * Original interest calculation logic preserved for backward compatibility.
     * This ensures behavior remains unchanged even without strategies configured.
     */
    private double calculateInterestOriginal(TimeDeposit timeDeposit) {
        double interest = 0;

        if (timeDeposit.getDays() > 30) {
            if (timeDeposit.getPlanType().equals("student")) {
                if (timeDeposit.getDays() < 366) {
                    interest = timeDeposit.getBalance() * 0.03 / 12;
                }
            } else if (timeDeposit.getPlanType().equals("premium")) {
                if (timeDeposit.getDays() > 45) {
                    interest = timeDeposit.getBalance() * 0.05 / 12;
                }
            } else if (timeDeposit.getPlanType().equals("basic")) {
                interest = timeDeposit.getBalance() * 0.01 / 12;
            }
        }

        return interest;
    }
}
