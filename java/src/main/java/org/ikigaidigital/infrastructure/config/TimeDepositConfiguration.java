package org.ikigaidigital.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TimeDepositProperties.class)
public class TimeDepositConfiguration {
    // TimeDepositCalculator is now provided by StrategyBasedTimeDepositCalculator component
}