package com.votamas.r2dbc.potentialvoter.mapper;

import com.votamas.model.potentialvoter.PollingPlace;
import com.votamas.model.potentialvoter.PotentialVoterDetails;
import com.votamas.model.potentialvoter.VotingTable;
import com.votamas.model.potentialvoter.VotingZone;
import com.votamas.r2dbc.potentialvoter.projections.PotentialVoterDetailsProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PotentialVoterDetailsMapper {

    @Mapping(target = "id", source = "potentialVoterId")
    @Mapping(target = "votingTable", expression = "java(toVotingTable(source))")
    PotentialVoterDetails toDomain(PotentialVoterDetailsProjection source);

    default VotingTable toVotingTable(PotentialVoterDetailsProjection source) {
        return source.votingTableId() == null ? null : mapVotingTable(source);
    }

    @Mapping(target = "id", source = "votingTableId")
    @Mapping(target = "pollingPlace", expression = "java(toPollingPlace(source))")
    VotingTable mapVotingTable(PotentialVoterDetailsProjection source);

    default PollingPlace toPollingPlace(PotentialVoterDetailsProjection source) {
        return source.pollingPlaceId() == null ? null : mapPollingPlace(source);
    }

    @Mapping(target = "id", source = "pollingPlaceId")
    @Mapping(target = "name", source = "pollingPlaceName")
    @Mapping(target = "votingZone", expression = "java(toVotingZone(source))")
    PollingPlace mapPollingPlace(PotentialVoterDetailsProjection source);

    default VotingZone toVotingZone(PotentialVoterDetailsProjection source) {
        return source.votingZoneId() == null ? null : mapVotingZone(source);
    }

    @Mapping(target = "id", source = "votingZoneId")
    @Mapping(target = "name", source = "votingZoneName")
    VotingZone mapVotingZone(PotentialVoterDetailsProjection source);
}
