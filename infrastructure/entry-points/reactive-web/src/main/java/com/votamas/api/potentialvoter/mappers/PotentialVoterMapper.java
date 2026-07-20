package com.votamas.api.potentialvoter.mappers;

import com.votamas.api.potentialvoter.dtos.PotentialVoterRequestDTO;
import com.votamas.api.potentialvoter.dtos.PotentialVoterResponseDTO;
import com.votamas.api.potentialvoter.dtos.PollingPlaceResponseDTO;
import com.votamas.api.potentialvoter.dtos.VotingTableResponseDTO;
import com.votamas.api.potentialvoter.dtos.VotingZoneResponseDTO;
import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.model.potentialvoter.PotentialVoterDetails;
import com.votamas.model.potentialvoter.PollingPlace;
import com.votamas.model.potentialvoter.VotingTable;
import com.votamas.model.potentialvoter.VotingZone;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface PotentialVoterMapper {
    PotentialVoterMapper INSTANCE = Mappers.getMapper(PotentialVoterMapper.class);

    PotentialVoter toPotentialVoter(PotentialVoterRequestDTO request);

    @Mapping(target = "votingTable", source = "votingTableId")
    PotentialVoterResponseDTO toResponse(PotentialVoter voter);

    PotentialVoterResponseDTO toResponse(PotentialVoterDetails voter);

    @Mapping(target = "pollingPlace", source = "pollingPlace")
    VotingTableResponseDTO toResponse(VotingTable votingTable);

    PollingPlaceResponseDTO toResponse(PollingPlace pollingPlace);

    VotingZoneResponseDTO toResponse(VotingZone votingZone);

    default VotingTableResponseDTO toVotingTableResponse(java.util.UUID id) {
        return id == null ? null : new VotingTableResponseDTO(id, null, null);
    }
}
