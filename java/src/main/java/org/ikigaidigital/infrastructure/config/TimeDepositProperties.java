package org.ikigaidigital.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "time-deposit")
public class TimeDepositProperties {

    private Map<String, PlanConfiguration> interestRates;

    @Data
    public static class PlanConfiguration {
        private double annualRate;
        private int minDays;
        private Integer maxDays; // Optional, only for student plan
    }

    public PlanConfiguration getPlanConfig(String planType) {
        return interestRates.get(planType.toLowerCase());
    }
}