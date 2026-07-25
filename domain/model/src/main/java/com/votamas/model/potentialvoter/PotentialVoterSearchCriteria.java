package com.votamas.model.potentialvoter;

import com.votamas.model.common.pagination.PageQuery;

import java.util.Objects;
import java.util.UUID;

public record PotentialVoterSearchCriteria(
        PageQuery pagination,
        String identification,
        UUID pollingPlaceId,
        UUID votingZoneId,
        UUID assignedLeaderId
) {
    public PotentialVoterSearchCriteria {
        Objects.requireNonNull(pagination, "La paginación es obligatoria");
    }

    public static PotentialVoterSearchCriteria withoutFilters(PageQuery pagination) {
        return new PotentialVoterSearchCriteria(pagination, null, null, null, null);
    }

    public PotentialVoterSearchCriteria assignedTo(UUID leaderId) {
        return new PotentialVoterSearchCriteria(
                pagination,
                identification,
                pollingPlaceId,
                votingZoneId,
                Objects.requireNonNull(leaderId, "El líder es obligatorio")
        );
    }
}
