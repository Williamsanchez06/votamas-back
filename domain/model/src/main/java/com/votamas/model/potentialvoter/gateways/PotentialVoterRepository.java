package com.votamas.model.potentialvoter.gateways;

import com.votamas.model.potentialvoter.PotentialVoter;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Set;
import java.util.UUID;

public interface PotentialVoterRepository {
    Mono<PotentialVoter> save(PotentialVoter potentialVoter);

    Mono<Boolean> existsByIdentification(String identification);

    Flux<String> findExistingIdentifications(Set<String> identifications);

    Mono<PotentialVoter> findById(UUID id);
}
