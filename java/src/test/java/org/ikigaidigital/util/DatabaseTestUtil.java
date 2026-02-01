package org.ikigaidigital.util;

import org.ikigaidigital.infrastructure.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.infrastructure.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.infrastructure.persistence.repository.JpaTimeDepositRepository;
import org.ikigaidigital.infrastructure.persistence.repository.JpaWithdrawalRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for database operations in tests.
 * Provides methods for setting up test data and cleaning the database.
 * Follows Single Responsibility Principle - only handles database operations.
 */
@Component
public class DatabaseTestUtil {

    private final JpaTimeDepositRepository timeDepositRepository;
    private final JpaWithdrawalRepository withdrawalRepository;

    public DatabaseTestUtil(JpaTimeDepositRepository timeDepositRepository,
                            JpaWithdrawalRepository withdrawalRepository) {
        this.timeDepositRepository = timeDepositRepository;
        this.withdrawalRepository = withdrawalRepository;
    }

    /**
     * Cleans all data from the database.
     * Order matters due to foreign key constraints.
     */
    @Transactional
    public void cleanDatabase() {
        withdrawalRepository.deleteAll();
        timeDepositRepository.deleteAll();
    }

    /**
     * Creates and saves a time deposit entity with default test values.
     */
    @Transactional
    public TimeDepositEntity createAndSaveTimeDeposit(String planType, BigDecimal balance, Integer days) {
        TimeDepositEntity entity = TestDataBuilder.aTimeDepositEntity()
                .withPlanType(planType)
                .withBalance(balance)
                .withDays(days)
                .build();
        return timeDepositRepository.save(entity);
    }

    /**
     * Creates and saves a withdrawal entity.
     */
    @Transactional
    public WithdrawalEntity createAndSaveWithdrawal(TimeDepositEntity timeDeposit,
                                                    BigDecimal amount,
                                                    LocalDate date) {
        WithdrawalEntity entity = TestDataBuilder.aWithdrawal()
                .withTimeDeposit(timeDeposit)
                .withAmount(amount)
                .withDate(date)
                .build();
        return withdrawalRepository.save(entity);
    }

    /**
     * Creates multiple time deposits with different plan types for testing.
     * Returns a list containing basic, student, and premium deposits.
     */
    @Transactional
    public List<TimeDepositEntity> createStandardTestDeposits() {
        List<TimeDepositEntity> deposits = new ArrayList<>();

        deposits.add(createAndSaveTimeDeposit(
                TestConstants.PlanTypes.BASIC,
                TestConstants.SampleBalances.BASIC_BALANCE,
                45
        ));

        deposits.add(createAndSaveTimeDeposit(
                TestConstants.PlanTypes.STUDENT,
                TestConstants.SampleBalances.STUDENT_BALANCE,
                180
        ));

        deposits.add(createAndSaveTimeDeposit(
                TestConstants.PlanTypes.PREMIUM,
                TestConstants.SampleBalances.PREMIUM_BALANCE,
                60
        ));

        return deposits;
    }

    /**
     * Creates test deposits with associated withdrawals.
     * Useful for testing the full data model.
     */
    @Transactional
    public TestDataWithWithdrawals createDepositsWithWithdrawals() {
        TimeDepositEntity deposit1 = createAndSaveTimeDeposit(
                TestConstants.PlanTypes.BASIC,
                TestConstants.SampleBalances.BASIC_BALANCE,
                45
        );

        TimeDepositEntity deposit2 = createAndSaveTimeDeposit(
                TestConstants.PlanTypes.STUDENT,
                TestConstants.SampleBalances.STUDENT_BALANCE,
                180
        );

        List<WithdrawalEntity> withdrawals = new ArrayList<>();

        // Create withdrawals for deposit1
        withdrawals.add(createAndSaveWithdrawal(
                deposit1,
                TestConstants.SampleWithdrawals.MEDIUM_WITHDRAWAL,
                LocalDate.now().minusDays(10)
        ));

        withdrawals.add(createAndSaveWithdrawal(
                deposit1,
                TestConstants.SampleWithdrawals.SMALL_WITHDRAWAL,
                LocalDate.now().minusDays(5)
        ));

        // Create withdrawal for deposit2
        withdrawals.add(createAndSaveWithdrawal(
                deposit2,
                TestConstants.SampleWithdrawals.SMALL_WITHDRAWAL,
                LocalDate.now().minusDays(15)
        ));

        return new TestDataWithWithdrawals(
                List.of(deposit1, deposit2),
                withdrawals
        );
    }

    /**
     * Creates multiple deposits of the same type for pagination testing.
     */
    @Transactional
    public List<TimeDepositEntity> createMultipleDeposits(int count, String planType) {
        List<TimeDepositEntity> deposits = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            deposits.add(createAndSaveTimeDeposit(
                    planType,
                    TestConstants.SampleBalances.SMALL_BALANCE,
                    45
            ));
        }

        return deposits;
    }

    /**
     * Data class to hold deposits with their associated withdrawals.
     */
    public static class TestDataWithWithdrawals {
        private final List<TimeDepositEntity> deposits;
        private final List<WithdrawalEntity> withdrawals;

        public TestDataWithWithdrawals(List<TimeDepositEntity> deposits,
                                       List<WithdrawalEntity> withdrawals) {
            this.deposits = deposits;
            this.withdrawals = withdrawals;
        }

        public List<TimeDepositEntity> getDeposits() {
            return deposits;
        }

        public List<WithdrawalEntity> getWithdrawals() {
            return withdrawals;
        }

        public TimeDepositEntity getFirstDeposit() {
            return deposits.isEmpty() ? null : deposits.get(0);
        }

        public TimeDepositEntity getDepositAt(int index) {
            return deposits.size() > index ? deposits.get(index) : null;
        }
    }
}