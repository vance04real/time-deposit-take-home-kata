package org.ikigaidigital.util;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.infrastructure.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.infrastructure.persistence.entity.WithdrawalEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Builder class for creating test data following the Builder pattern.
 * Provides fluent API for creating test entities with sensible defaults.
 */
public class TestDataBuilder {

    /**
     * Builder for TimeDeposit domain objects
     */
    public static class TimeDepositBuilder {
        private Integer id = 1;
        private String planType = "basic";
        private Double balance = 10000.00;
        private Integer days = 45;

        public TimeDepositBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public TimeDepositBuilder withPlanType(String planType) {
            this.planType = planType;
            return this;
        }

        public TimeDepositBuilder withBalance(Double balance) {
            this.balance = balance;
            return this;
        }

        public TimeDepositBuilder withDays(Integer days) {
            this.days = days;
            return this;
        }

        public TimeDepositBuilder asBasicPlan() {
            this.planType = "basic";
            return this;
        }

        public TimeDepositBuilder asStudentPlan() {
            this.planType = "student";
            return this;
        }

        public TimeDepositBuilder asPremiumPlan() {
            this.planType = "premium";
            return this;
        }

        public TimeDeposit build() {
            return new TimeDeposit(id, planType, balance, days);
        }
    }

    /**
     * Builder for TimeDepositEntity persistence objects
     */
    public static class TimeDepositEntityBuilder {
        private Integer id = null;
        private String planType = "basic";
        private BigDecimal balance = new BigDecimal("10000.00");
        private Integer days = 45;

        public TimeDepositEntityBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public TimeDepositEntityBuilder withPlanType(String planType) {
            this.planType = planType;
            return this;
        }

        public TimeDepositEntityBuilder withBalance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public TimeDepositEntityBuilder withBalance(String balance) {
            this.balance = new BigDecimal(balance);
            return this;
        }

        public TimeDepositEntityBuilder withDays(Integer days) {
            this.days = days;
            return this;
        }

        public TimeDepositEntityBuilder asBasicPlan() {
            this.planType = "basic";
            return this;
        }

        public TimeDepositEntityBuilder asStudentPlan() {
            this.planType = "student";
            return this;
        }

        public TimeDepositEntityBuilder asPremiumPlan() {
            this.planType = "premium";
            return this;
        }

        public TimeDepositEntity build() {
            TimeDepositEntity entity = new TimeDepositEntity();
            if (id != null) {
                entity.setId(id);
            }
            entity.setPlanType(planType);
            entity.setBalance(balance);
            entity.setDays(days);
            return entity;
        }
    }

    /**
     * Builder for WithdrawalEntity persistence objects
     */
    public static class WithdrawalEntityBuilder {
        private Integer id = null;
        private TimeDepositEntity timeDeposit;
        private BigDecimal amount = new BigDecimal("500.00");
        private LocalDate date = LocalDate.now();

        public WithdrawalEntityBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public WithdrawalEntityBuilder withTimeDeposit(TimeDepositEntity timeDeposit) {
            this.timeDeposit = timeDeposit;
            return this;
        }

        public WithdrawalEntityBuilder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public WithdrawalEntityBuilder withAmount(String amount) {
            this.amount = new BigDecimal(amount);
            return this;
        }

        public WithdrawalEntityBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public WithdrawalEntityBuilder daysAgo(int days) {
            this.date = LocalDate.now().minusDays(days);
            return this;
        }

        public WithdrawalEntity build() {
            if (timeDeposit == null) {
                throw new IllegalStateException("TimeDeposit must be set for WithdrawalEntity");
            }

            WithdrawalEntity entity = new WithdrawalEntity();
            if (id != null) {
                entity.setId(id);
            }
            entity.setTimeDeposit(timeDeposit);
            entity.setAmount(amount);
            entity.setDate(date);
            return entity;
        }
    }

    // Static factory methods for convenient access
    public static TimeDepositBuilder aTimeDeposit() {
        return new TimeDepositBuilder();
    }

    public static TimeDepositEntityBuilder aTimeDepositEntity() {
        return new TimeDepositEntityBuilder();
    }

    public static WithdrawalEntityBuilder aWithdrawal() {
        return new WithdrawalEntityBuilder();
    }

    // Pre-configured builders for common scenarios
    public static TimeDepositEntityBuilder aBasicTimeDeposit() {
        return aTimeDepositEntity()
                .asBasicPlan()
                .withBalance("10000.00")
                .withDays(45);
    }

    public static TimeDepositEntityBuilder aStudentTimeDeposit() {
        return aTimeDepositEntity()
                .asStudentPlan()
                .withBalance("5000.00")
                .withDays(180);
    }

    public static TimeDepositEntityBuilder aPremiumTimeDeposit() {
        return aTimeDepositEntity()
                .asPremiumPlan()
                .withBalance("20000.00")
                .withDays(60);
    }
}