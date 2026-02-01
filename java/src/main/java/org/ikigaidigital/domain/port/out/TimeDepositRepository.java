package org.ikigaidigital.domain.port.out;

import org.ikigaidigital.TimeDeposit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TimeDepositRepository {
    List<TimeDeposit> findAll();

    Page<TimeDeposit> findAll(Pageable pageable);

    void saveAll(List<TimeDeposit> timeDeposits);

    long count();
}