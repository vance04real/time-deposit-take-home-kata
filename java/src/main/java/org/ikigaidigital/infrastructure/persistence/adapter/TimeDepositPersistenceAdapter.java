package org.ikigaidigital.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.port.out.TimeDepositRepository;
import org.ikigaidigital.infrastructure.persistence.mapper.TimeDepositMapper;
import org.ikigaidigital.infrastructure.persistence.repository.JpaTimeDepositRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TimeDepositPersistenceAdapter implements TimeDepositRepository {

    private final JpaTimeDepositRepository jpaTimeDepositRepository;
    private final TimeDepositMapper timeDepositMapper;

    @Override
    public List<TimeDeposit> findAll() {
        return jpaTimeDepositRepository.findAll().stream()
                .map(timeDepositMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TimeDeposit> findAll(Pageable pageable) {
        return jpaTimeDepositRepository.findAll(pageable)
                .map(timeDepositMapper::toDomain);
    }

    @Override
    public void saveAll(List<TimeDeposit> timeDeposits) {
        var entities = timeDeposits.stream()
                .map(td -> {
                    // Find existing entity or create new one
                    var entity = jpaTimeDepositRepository.findById(td.getId())
                            .orElse(timeDepositMapper.toEntity(td));

                    // Update balance
                    entity.setBalance(timeDepositMapper.doubleToBigDecimal(td.getBalance()));

                    return entity;
                })
                .collect(Collectors.toList());

        jpaTimeDepositRepository.saveAll(entities);
    }

    @Override
    public long count() {
        return jpaTimeDepositRepository.count();
    }
}