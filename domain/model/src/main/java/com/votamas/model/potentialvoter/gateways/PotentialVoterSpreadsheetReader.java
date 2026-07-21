package com.votamas.model.potentialvoter.gateways;

import com.votamas.model.potentialvoter.PotentialVoterImportData;
import reactor.core.publisher.Mono;

public interface PotentialVoterSpreadsheetReader {
    Mono<PotentialVoterImportData> read(byte[] content);
}
