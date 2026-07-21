package com.votamas.r2dbc.potentialvoter;

import com.votamas.r2dbc.potentialvoter.entities.PotentialVoterData;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import java.util.Collection;
import java.util.UUID;

public interface PotentialVoterReactiveRepository extends ReactiveCrudRepository<PotentialVoterData, UUID> {
    Mono<Boolean> existsByIdentification(String identification);

    Flux<PotentialVoterData> findAllByIdentificationIn(Collection<String> identifications);
}
