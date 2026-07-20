package com.votamas.api.potentialvoter.dtos;

import java.util.UUID;

public record PollingPlaceResponseDTO(
        UUID id,
        String name,
        VotingZoneResponseDTO votingZone
) {
}
