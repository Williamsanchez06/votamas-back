package com.votamas.usecase.potentialvoter;

import com.votamas.model.exception.MessageError;
import com.votamas.model.exception.NotFoundException;
import com.votamas.model.potentialvoter.VotingZone;
import com.votamas.model.potentialvoter.VotingZoneDetails;
import com.votamas.model.potentialvoter.gateways.VotingLocationRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class VotingLocationUseCase {
    private final VotingLocationRepository repository;

    public Flux<VotingZone> getVotingZones() {
        return repository.findAllZones();
    }

    public Mono<VotingZoneDetails> getVotingZone(UUID zoneId) {
        return repository.findZoneDetailsById(zoneId)
                .switchIfEmpty(Mono.error(new NotFoundException(MessageError.NO_VOTING_ZONE_FOUND)));
    }
}
