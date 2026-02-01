package org.ikigaidigital.infrastructure.persistence.mapper;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.infrastructure.persistence.entity.TimeDepositEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface TimeDepositMapper {

    // ASSUMPTION: Withdrawals are managed separately and not mapped directly
    // This maintains separation between aggregate boundaries
    @Mapping(target = "withdrawals", ignore = true)
    TimeDepositEntity toEntity(TimeDeposit domain);

    TimeDeposit toDomain(TimeDepositEntity entity);

    // ASSUMPTION: Domain model uses Double for balance (legacy code constraint)
    // while persistence uses BigDecimal for precision
    default BigDecimal doubleToBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }

    default Double bigDecimalToDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }
}