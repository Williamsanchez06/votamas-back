package com.votamas.model.potentialvoter;

import java.util.UUID;

public record VotingTableReference(
        UUID id,
        String votingZoneName,
        String pollingPlaceName,
        int tableNumber
) {
}
