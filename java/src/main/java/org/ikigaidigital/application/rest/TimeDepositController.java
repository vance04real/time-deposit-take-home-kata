package org.ikigaidigital.application.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ikigaidigital.application.rest.dto.ErrorResponse;
import org.ikigaidigital.application.rest.dto.TimeDepositsPageResponse;
import org.ikigaidigital.application.rest.dto.UpdateBalancesResponse;
import org.ikigaidigital.application.rest.mapper.TimeDepositApiMapper;
import org.ikigaidigital.domain.port.in.GetTimeDepositsUseCase;
import org.ikigaidigital.domain.port.in.UpdateBalancesUseCase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/time-deposits")
@RequiredArgsConstructor
@Tag(name = "Time Deposits", description = "Endpoints for managing time deposits")
public class TimeDepositController {

    private final UpdateBalancesUseCase updateBalancesUseCase;
    private final GetTimeDepositsUseCase getTimeDepositsUseCase;
    private final TimeDepositApiMapper apiMapper;

    @PostMapping("/update-balances")
    @Operation(
            summary = "Update balances for all time deposits",
            description = """
                    Calculates and applies monthly interest to all time deposits based on their plan type:
                    - Basic Plan: 1% interest
                    - Student Plan: 3% interest (no interest after 1 year)
                    - Premium Plan: 5% interest (interest starts after 45 days)
                    
                    No interest is applied for the first 30 days for any plan.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balances updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateBalancesResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<UpdateBalancesResponse> updateBalances() {
        var result = updateBalancesUseCase.updateAllBalances();
        var response = apiMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Retrieve all time deposits",
            description = "Returns a list of all time deposits with their current details and associated withdrawals"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of time deposits retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TimeDepositsPageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<TimeDepositsPageResponse> getAllTimeDeposits(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        // Parse sort parameter
        var sortParams = sort.split(",");
        var sortField = sortParams[0];
        var sortDirection = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        var pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        var timeDepositsPage = getTimeDepositsUseCase.getAllTimeDeposits(pageable);

        var response = apiMapper.toPageResponse(timeDepositsPage);
        return ResponseEntity.ok(response);
    }
}