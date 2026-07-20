package com.votamas.api.potentialvoter.dtos;

import java.util.List;
import java.util.UUID;

public record VotingLocationResponseDTO(
        UUID id,
        String name,
        List<PollingPlaceDTO> pollingPlaces
) {
    public record PollingPlaceDTO(UUID id, String name, List<VotingTableDTO> votingTables) {
    }

    public record VotingTableDTO(UUID id, Integer tableNumber) {
    }
}
