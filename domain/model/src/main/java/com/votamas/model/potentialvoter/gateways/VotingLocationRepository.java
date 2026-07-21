package com.votamas.model.potentialvoter.gateways;

import com.votamas.model.potentialvoter.VotingZone;
import com.votamas.model.potentialvoter.VotingZoneDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VotingLocationRepository {
    Flux<VotingZone> findAllZones();

    Mono<VotingZoneDetails> findZoneDetailsById(UUID zoneId);

    Mono<UUID> findVotingTableId(String votingZoneName, String pollingPlaceName, int tableNumber);
}
