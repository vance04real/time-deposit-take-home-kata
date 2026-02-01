package org.ikigaidigital.infrastructure.persistence.mapper;

import org.ikigaidigital.domain.model.Withdrawal;
import org.ikigaidigital.infrastructure.persistence.entity.WithdrawalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WithdrawalMapper {

    @Mapping(source = "timeDeposit.id", target = "timeDepositId")
    Withdrawal toDomain(WithdrawalEntity entity);

    @Mapping(target = "timeDeposit", ignore = true)
    WithdrawalEntity toEntity(Withdrawal domain);
}