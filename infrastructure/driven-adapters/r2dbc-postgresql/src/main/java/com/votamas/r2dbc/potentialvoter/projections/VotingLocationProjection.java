package com.votamas.r2dbc.potentialvoter.projections;

import java.util.UUID;

public record VotingLocationProjection(
        UUID votingZoneId,
        String votingZoneName,
        UUID pollingPlaceId,
        String pollingPlaceName,
        UUID votingTableId,
        Integer tableNumber
) {
}
