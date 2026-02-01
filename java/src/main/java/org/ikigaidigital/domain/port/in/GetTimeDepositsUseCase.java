package org.ikigaidigital.domain.port.in;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.model.Withdrawal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GetTimeDepositsUseCase {
    Page<TimeDepositWithWithdrawals> getAllTimeDeposits(Pageable pageable);

    record TimeDepositWithWithdrawals(
            Integer id,
            String planType,
            Double balance,
            Integer days,
            List<Withdrawal> withdrawals
    ) {
        public static TimeDepositWithWithdrawals from(TimeDeposit timeDeposit, List<Withdrawal> withdrawals) {
            return new TimeDepositWithWithdrawals(
                    timeDeposit.getId(),
                    timeDeposit.getPlanType(),
                    timeDeposit.getBalance(),
                    timeDeposit.getDays(),
                    withdrawals
            );
        }
    }
}