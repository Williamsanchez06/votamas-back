package com.votamas.model.potentialvoter.gateways;

import com.votamas.model.potentialvoter.PotentialVoter;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PotentialVoterRepository {
    Mono<PotentialVoter> save(PotentialVoter potentialVoter);

    Mono<Boolean> existsByIdentification(String identification);

    Mono<PotentialVoter> findById(UUID id);
}
