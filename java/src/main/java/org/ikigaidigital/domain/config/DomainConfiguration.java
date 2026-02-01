package org.ikigaidigital.domain.config;

import org.ikigaidigital.TimeDepositCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {

    @Bean
    public TimeDepositCalculator timeDepositCalculator() {
        return new TimeDepositCalculator();
    }
}