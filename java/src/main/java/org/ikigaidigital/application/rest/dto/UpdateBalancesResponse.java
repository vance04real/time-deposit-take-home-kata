package org.ikigaidigital.application.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBalancesResponse {
    private String message;
    private Integer updatedCount;
    private String timestamp;
}