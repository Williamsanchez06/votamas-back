package com.votamas.api.potentialvoter.mappers;

import com.votamas.api.potentialvoter.dtos.PotentialVoterRequestDTO;
import com.votamas.model.potentialvoter.PotentialVoter;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
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
}
