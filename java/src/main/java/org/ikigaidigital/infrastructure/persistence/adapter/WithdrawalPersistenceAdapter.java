package org.ikigaidigital.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.domain.model.Withdrawal;
import org.ikigaidigital.domain.port.out.WithdrawalRepository;
import org.ikigaidigital.infrastructure.persistence.mapper.WithdrawalMapper;
import org.ikigaidigital.infrastructure.persistence.repository.JpaWithdrawalRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WithdrawalPersistenceAdapter implements WithdrawalRepository {

    private final JpaWithdrawalRepository jpaWithdrawalRepository;
    private final WithdrawalMapper withdrawalMapper;

    @Override
    public List<Withdrawal> findByTimeDepositId(Integer timeDepositId) {
        return jpaWithdrawalRepository.findByTimeDepositId(timeDepositId).stream()
                .map(withdrawalMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Withdrawal> findByTimeDepositIdIn(List<Integer> timeDepositIds) {
        return jpaWithdrawalRepository.findByTimeDepositIdIn(timeDepositIds).stream()
                .map(withdrawalMapper::toDomain)
                .collect(Collectors.toList());
    }
}