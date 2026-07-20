package com.votamas.api.potentialvoter.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record PotentialVoterResponseDTO(
        UUID id,
        String identification,
        String firstName,
        String lastName,
        UUID votingTableId,
        LocalDate registrationDate,
        UUID assignedLeaderId
) {
}
