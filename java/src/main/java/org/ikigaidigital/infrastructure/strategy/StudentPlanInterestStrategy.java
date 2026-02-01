package org.ikigaidigital.infrastructure.strategy;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.strategy.InterestCalculationStrategy;
import org.ikigaidigital.infrastructure.config.TimeDepositProperties;
import org.springframework.stereotype.Component;

/**
 * Interest calculation strategy for student plan time deposits.
 * Interest is applied between minimum and maximum days constraints.
 */
@Component
@RequiredArgsConstructor
public class StudentPlanInterestStrategy implements InterestCalculationStrategy {

    private final TimeDepositProperties timeDepositProperties;

    @Override
    public double calculateInterest(TimeDeposit timeDeposit) {
        var planConfig = timeDepositProperties.getPlanConfig("student");

        // Check minimum days requirement
        if (timeDeposit.getDays() <= planConfig.getMinDays()) {
            return 0;
        }

        // Check maximum days constraint
        if (planConfig.getMaxDays() != null && timeDeposit.getDays() > planConfig.getMaxDays()) {
            return 0;
        }

        // Calculate monthly interest
        return timeDeposit.getBalance() * planConfig.getAnnualRate() / 12;
    }

    @Override
    public boolean supports(String planType) {
        return "student".equalsIgnoreCase(planType);
    }
}