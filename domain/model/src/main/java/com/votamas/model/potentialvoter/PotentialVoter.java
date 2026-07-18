package com.votamas.model.potentialvoter;

import lombok.Builder;
import java.time.LocalDate;
import java.util.UUID;

@Builder(toBuilder = true)
public record PotentialVoter(
        UUID id,
        String identification,
        String firstName,
        String lastName,
        UUID votingTableId,
        LocalDate registrationDate,
        UUID assignedLeaderId
) {}
