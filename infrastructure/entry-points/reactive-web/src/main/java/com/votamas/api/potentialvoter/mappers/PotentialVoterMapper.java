package com.votamas.api.potentialvoter.mappers;

import com.votamas.api.potentialvoter.dtos.PotentialVoterCreateRequestDTO;
import com.votamas.api.potentialvoter.dtos.PotentialVoterUpdateRequestDTO;
import com.votamas.api.potentialvoter.dtos.PotentialVoterResponseDTO;
import com.votamas.api.potentialvoter.dtos.PollingPlaceResponseDTO;
import com.votamas.api.potentialvoter.dtos.VotingTableResponseDTO;
import com.votamas.api.potentialvoter.dtos.VotingZoneResponseDTO;
import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.model.potentialvoter.PotentialVoterDetails;
import com.votamas.model.potentialvoter.PollingPlace;
import com.votamas.model.potentialvoter.VotingTable;
import com.votamas.model.potentialvoter.VotingZone;
import com.votamas.model.potentialvoter.PotentialVoterImportResult;
import com.votamas.api.potentialvoter.dtos.PotentialVoterImportResponseDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface PotentialVoterMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "assignedLeaderId", ignore = true)
    PotentialVoter toPotentialVoter(PotentialVoterCreateRequestDTO request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "identification", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "assignedLeaderId", ignore = true)
    PotentialVoter toPotentialVoter(PotentialVoterUpdateRequestDTO request);

    PotentialVoterResponseDTO toResponse(PotentialVoterDetails voter);

    PotentialVoterImportResponseDTO toResponse(PotentialVoterImportResult result);

    @Mapping(target = "pollingPlace", source = "pollingPlace")
    VotingTableResponseDTO toResponse(VotingTable votingTable);

    PollingPlaceResponseDTO toResponse(PollingPlace pollingPlace);

    VotingZoneResponseDTO toResponse(VotingZone votingZone);

}
