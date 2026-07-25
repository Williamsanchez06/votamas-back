package com.votamas.model.potentialvoter;

import java.time.LocalDate;

public record PotentialVoterExportRow(
        String identification,
        String firstName,
        String lastName,
        String votingZoneName,
        String pollingPlaceName,
        Integer tableNumber,
        LocalDate registrationDate,
        String assignedLeaderName
) {
}
