package org.ikigaidigital.domain.config;

import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.domain.strategy.InterestCalculationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DomainConfiguration {

    /**
     * Creates TimeDepositCalculator with all available strategies injected.
     * This enables the Strategy Pattern for interest calculations while
     * maintaining backward compatibility.
     *
     * @param strategies All available interest calculation strategies
     * @return Configured TimeDepositCalculator
     */
    @Bean
    public TimeDepositCalculator timeDepositCalculator(List<InterestCalculationStrategy> strategies) {
        return new TimeDepositCalculator(strategies);
    }
}