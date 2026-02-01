package org.ikigaidigital.domain.port.in;

public interface UpdateBalancesUseCase {
    UpdateBalancesResult updateAllBalances();

    record UpdateBalancesResult(String message, int updatedCount, String timestamp) {
    }
}