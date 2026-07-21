package com.votamas.api.potentialvoter.mappers;

import com.votamas.api.potentialvoter.dtos.VotingLocationResponseDTO;
import com.votamas.api.potentialvoter.dtos.VotingZoneResponseDTO;
import com.votamas.model.potentialvoter.VotingZone;
import com.votamas.model.potentialvoter.VotingZoneDetails;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface VotingLocationMapper {
    VotingZoneResponseDTO toResponse(VotingZone zone);

    VotingLocationResponseDTO toResponse(VotingZoneDetails zone);
}
