package org.ikigaidigital.application.rest;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.api.generated.TimeDepositsApi;
import org.ikigaidigital.api.generated.model.TimeDepositsResponse;
import org.ikigaidigital.api.generated.model.UpdateBalancesResponse;
import org.ikigaidigital.application.rest.mapper.TimeDepositApiMapper;
import org.ikigaidigital.domain.port.in.GetTimeDepositsUseCase;
import org.ikigaidigital.domain.port.in.UpdateBalancesUseCase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TimeDepositController implements TimeDepositsApi {

    private final UpdateBalancesUseCase updateBalancesUseCase;
    private final GetTimeDepositsUseCase getTimeDepositsUseCase;
    private final TimeDepositApiMapper apiMapper;

    @Override
    public ResponseEntity<UpdateBalancesResponse> updateTimeDepositBalances() {
        var result = updateBalancesUseCase.updateAllBalances();
        var response = apiMapper.toGeneratedResponse(result);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TimeDepositsResponse> getAllTimeDeposits(
            Integer page,
            Integer size,
            String sort
    ) {
        // Parse sort parameter
        var sortParams = sort.split(",");
        var sortField = sortParams[0];
        var sortDirection = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        var pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        var timeDepositsPage = getTimeDepositsUseCase.getAllTimeDeposits(pageable);

        var response = apiMapper.toGeneratedTimeDepositsResponse(timeDepositsPage);
        return ResponseEntity.ok(response);
    }
}