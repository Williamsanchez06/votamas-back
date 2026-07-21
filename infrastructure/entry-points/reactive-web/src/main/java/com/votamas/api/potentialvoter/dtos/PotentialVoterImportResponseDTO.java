package com.votamas.api.potentialvoter.dtos;

import java.util.List;

public record PotentialVoterImportResponseDTO(
        int totalRows,
        int successfulRows,
        int failedRows,
        int skippedRows,
        List<PotentialVoterImportErrorDTO> errors
) {
}
