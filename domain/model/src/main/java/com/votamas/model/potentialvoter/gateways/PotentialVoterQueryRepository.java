package com.votamas.model.potentialvoter.gateways;

import com.votamas.model.common.pagination.PageRequest;
import com.votamas.model.common.pagination.PageResult;
import com.votamas.model.potentialvoter.PotentialVoterDetails;
import reactor.core.publisher.Mono;

public interface PotentialVoterQueryRepository {
    Mono<PageResult<PotentialVoterDetails>> findAllWithVotingLocation(PageRequest pageRequest);
}
