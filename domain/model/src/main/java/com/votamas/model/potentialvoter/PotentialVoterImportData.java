package com.votamas.model.potentialvoter;

import java.util.List;

public record PotentialVoterImportData(List<PotentialVoterImportRow> rows, int skippedRows) {
    public PotentialVoterImportData {
        rows = List.copyOf(rows);
    }
}
