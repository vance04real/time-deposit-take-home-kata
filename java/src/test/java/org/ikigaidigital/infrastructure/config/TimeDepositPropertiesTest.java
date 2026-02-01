package org.ikigaidigital.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TimeDepositPropertiesTest.TestConfig.class)
@TestPropertySource(properties = {
        "time-deposit.interest-rates.basic.annual-rate=0.01",
        "time-deposit.interest-rates.basic.min-days=30",
        "time-deposit.interest-rates.student.annual-rate=0.03",
        "time-deposit.interest-rates.student.min-days=30",
        "time-deposit.interest-rates.student.max-days=365",
        "time-deposit.interest-rates.premium.annual-rate=0.05",
        "time-deposit.interest-rates.premium.min-days=45"
})
@DisplayName("Time Deposit Properties Configuration Tests")
class TimeDepositPropertiesTest {

    @Autowired
    private TimeDepositProperties properties;

    @Test
    @DisplayName("Should load basic plan configuration correctly")
    void shouldLoadBasicPlanConfiguration() {
        var basicConfig = properties.getPlanConfig("basic");

        assertThat(basicConfig).isNotNull();
        assertThat(basicConfig.getAnnualRate()).isEqualTo(0.01);
        assertThat(basicConfig.getMinDays()).isEqualTo(30);
        assertThat(basicConfig.getMaxDays()).isNull();
    }

    @Test
    @DisplayName("Should load student plan configuration correctly")
    void shouldLoadStudentPlanConfiguration() {
        var studentConfig = properties.getPlanConfig("student");

        assertThat(studentConfig).isNotNull();
        assertThat(studentConfig.getAnnualRate()).isEqualTo(0.03);
        assertThat(studentConfig.getMinDays()).isEqualTo(30);
        assertThat(studentConfig.getMaxDays()).isEqualTo(365);
    }

    @Test
    @DisplayName("Should load premium plan configuration correctly")
    void shouldLoadPremiumPlanConfiguration() {
        var premiumConfig = properties.getPlanConfig("premium");

        assertThat(premiumConfig).isNotNull();
        assertThat(premiumConfig.getAnnualRate()).isEqualTo(0.05);
        assertThat(premiumConfig.getMinDays()).isEqualTo(45);
        assertThat(premiumConfig.getMaxDays()).isNull();
    }

    @Test
    @DisplayName("Should handle case-insensitive plan names")
    void shouldHandleCaseInsensitivePlanNames() {
        assertThat(properties.getPlanConfig("BASIC")).isNotNull();
        assertThat(properties.getPlanConfig("Basic")).isNotNull();
        assertThat(properties.getPlanConfig("student")).isNotNull();
        assertThat(properties.getPlanConfig("STUDENT")).isNotNull();
    }

    @EnableConfigurationProperties(TimeDepositProperties.class)
    static class TestConfig {
        // Test configuration class
    }
}