package org.ikigaidigital.application.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeDepositsPageResponse {
    private List<TimeDepositResponse> content;
    private Long totalElements;
    private Integer totalPages;
    private Integer pageNumber;
    private Integer pageSize;
}