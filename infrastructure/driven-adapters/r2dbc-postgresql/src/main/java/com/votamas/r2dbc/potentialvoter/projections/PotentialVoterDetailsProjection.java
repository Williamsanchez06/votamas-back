package com.votamas.r2dbc.potentialvoter.projections;

import java.time.LocalDate;
import java.util.UUID;

public record PotentialVoterDetailsProjection(
        UUID potentialVoterId,
        String identification,
        String firstName,
        String lastName,
        LocalDate registrationDate,
        UUID assignedLeaderId,
        UUID votingTableId,
        Integer tableNumber,
        UUID pollingPlaceId,
        String pollingPlaceName,
        UUID votingZoneId,
        String votingZoneName
) {
}
