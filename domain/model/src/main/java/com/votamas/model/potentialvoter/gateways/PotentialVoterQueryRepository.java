package com.votamas.model.potentialvoter.gateways;

import com.votamas.model.common.pagination.PageResult;
import com.votamas.model.potentialvoter.PotentialVoterDetails;
import com.votamas.model.potentialvoter.PotentialVoterExportRow;
import com.votamas.model.potentialvoter.PotentialVoterSearchCriteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PotentialVoterQueryRepository {
    Mono<PageResult<PotentialVoterDetails>> findAllWithVotingLocation(PotentialVoterSearchCriteria criteria);

    Mono<PotentialVoterDetails> findByIdWithVotingLocation(UUID id);

    Flux<PotentialVoterExportRow> findForExport(PotentialVoterSearchCriteria criteria, int limit);
}
