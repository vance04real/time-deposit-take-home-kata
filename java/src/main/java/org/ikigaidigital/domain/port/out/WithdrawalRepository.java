package org.ikigaidigital.domain.port.out;

import org.ikigaidigital.domain.model.Withdrawal;

import java.util.List;

public interface WithdrawalRepository {
    List<Withdrawal> findByTimeDepositId(Integer timeDepositId);

    List<Withdrawal> findByTimeDepositIdIn(List<Integer> timeDepositIds);
}