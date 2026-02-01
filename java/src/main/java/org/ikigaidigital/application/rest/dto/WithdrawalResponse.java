package org.ikigaidigital.application.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalResponse {
    private Integer id;
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}