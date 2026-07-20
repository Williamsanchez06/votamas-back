package com.votamas.api.potentialvoter.dtos;

import java.util.UUID;

public record VotingTableResponseDTO(
        UUID id,
        Integer tableNumber,
        PollingPlaceResponseDTO pollingPlace
) {
}
