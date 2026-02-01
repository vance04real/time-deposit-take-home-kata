package org.ikigaidigital.domain.strategy;

import org.ikigaidigital.TimeDeposit;

/**
 * Strategy interface for calculating interest on time deposits.
 * Different implementations can be provided for different plan types.
 */
public interface InterestCalculationStrategy {

    /**
     * Calculates the interest for a given time deposit.
     *
     * @param timeDeposit the time deposit to calculate interest for
     * @return the calculated interest amount
     */
    double calculateInterest(TimeDeposit timeDeposit);

    /**
     * Checks if this strategy applies to the given plan type.
     *
     * @param planType the plan type to check
     * @return true if this strategy applies to the plan type
     */
    boolean supports(String planType);
}