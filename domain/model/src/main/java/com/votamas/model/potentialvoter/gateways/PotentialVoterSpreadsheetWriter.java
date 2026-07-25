package com.votamas.model.potentialvoter.gateways;

import com.votamas.model.potentialvoter.PotentialVoterExportRow;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PotentialVoterSpreadsheetWriter {
    Mono<byte[]> write(List<PotentialVoterExportRow> rows);
}
