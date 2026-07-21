package com.votamas.model.potentialvoter;

import java.util.List;

public record PotentialVoterImportResult(
        int totalRows,
        int successfulRows,
        int failedRows,
        int skippedRows,
        List<PotentialVoterImportError> errors
) {
    public PotentialVoterImportResult {
        errors = List.copyOf(errors);
    }
}
