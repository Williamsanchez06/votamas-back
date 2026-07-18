package com.votamas.model.potentialvoter.gateways;

import com.votamas.model.common.pagination.PageRequest;
import com.votamas.model.common.pagination.PageResult;
import com.votamas.model.potentialvoter.PotentialVoter;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PotentialVoterRepository {
    Mono<PotentialVoter> save(PotentialVoter potentialVoter);

    Mono<Boolean> existsByIdentification(String identification);

    Mono<PageResult<PotentialVoter>> findAll(PageRequest pageRequest);

    Mono<PotentialVoter> findById(UUID id);
}
