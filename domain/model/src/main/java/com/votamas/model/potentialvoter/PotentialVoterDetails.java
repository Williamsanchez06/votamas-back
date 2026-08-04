package com.votamas.model.potentialvoter;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record PotentialVoterDetails(
        UUID id,
        String identification,
        String firstName,
        String lastName,
        VotingTable votingTable,
        LocalDate registrationDate,
        String assignedLeaderName
) {
}
