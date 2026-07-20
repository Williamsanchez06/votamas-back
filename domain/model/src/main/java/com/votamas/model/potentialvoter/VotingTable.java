package com.votamas.model.potentialvoter;

import java.util.UUID;


public record VotingTable(
        UUID id,
        PollingPlace pollingPlace,
        Integer tableNumber
) {
}
