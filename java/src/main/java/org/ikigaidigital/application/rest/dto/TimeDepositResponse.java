package org.ikigaidigital.application.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeDepositResponse {
    private Integer id;
    private String planType;
    private Double balance;
    private Integer days;
    private List<WithdrawalResponse> withdrawals;
}