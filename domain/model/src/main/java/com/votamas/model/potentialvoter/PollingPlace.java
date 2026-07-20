package com.votamas.model.potentialvoter;

import java.util.UUID;

public record PollingPlace(UUID id, VotingZone votingZone, String name) {
}
