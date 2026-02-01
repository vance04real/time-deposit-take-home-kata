package org.ikigaidigital.infrastructure.persistence.repository;

import org.ikigaidigital.infrastructure.persistence.entity.WithdrawalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaWithdrawalRepository extends JpaRepository<WithdrawalEntity, Integer> {

    @Query("SELECT w FROM WithdrawalEntity w WHERE w.timeDeposit.id = :timeDepositId")
    List<WithdrawalEntity> findByTimeDepositId(@Param("timeDepositId") Integer timeDepositId);

    @Query("SELECT w FROM WithdrawalEntity w WHERE w.timeDeposit.id IN :timeDepositIds")
    List<WithdrawalEntity> findByTimeDepositIdIn(@Param("timeDepositIds") List<Integer> timeDepositIds);
}