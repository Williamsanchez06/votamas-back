package com.votamas.api.potentialvoter.dtos;

import java.util.UUID;

public record PotentialVoterRequestDTO(
        String identification,
        String firstName,
        String lastName,
        UUID votingTableId,
        UUID assignedLeaderId
) {}
