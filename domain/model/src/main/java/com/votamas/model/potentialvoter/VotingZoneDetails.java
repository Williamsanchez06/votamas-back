package com.votamas.model.potentialvoter;

import java.util.List;
import java.util.UUID;

public record VotingZoneDetails(UUID id, String name, List<PollingPlaceDetails> pollingPlaces) {
    public VotingZoneDetails {
        pollingPlaces = List.copyOf(pollingPlaces);
    }

    public record PollingPlaceDetails(UUID id, String name, List<VotingTableDetails> votingTables) {
        public PollingPlaceDetails {
            votingTables = List.copyOf(votingTables);
        }
    }

    public record VotingTableDetails(UUID id, Integer tableNumber) {
    }
}
