package org.ikigaidigital.infrastructure.strategy;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.strategy.InterestCalculationStrategy;
import org.ikigaidigital.infrastructure.config.TimeDepositProperties;
import org.springframework.stereotype.Component;

/**
 * Interest calculation strategy for premium plan time deposits.
 * Interest is applied after minimum days requirement is met.
 */
@Component
@RequiredArgsConstructor
public class PremiumPlanInterestStrategy implements InterestCalculationStrategy {

    private final TimeDepositProperties timeDepositProperties;

    @Override
    public double calculateInterest(TimeDeposit timeDeposit) {
        var planConfig = timeDepositProperties.getPlanConfig("premium");

        if (timeDeposit.getDays() <= planConfig.getMinDays()) {
            return 0;
        }

        // Calculate monthly interest
        return timeDeposit.getBalance() * planConfig.getAnnualRate() / 12;
    }

    @Override
    public boolean supports(String planType) {
        return "premium".equalsIgnoreCase(planType);
    }
}