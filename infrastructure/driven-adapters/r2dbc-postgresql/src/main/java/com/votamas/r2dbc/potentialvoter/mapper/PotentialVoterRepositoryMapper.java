package com.votamas.r2dbc.potentialvoter.mapper;

import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.r2dbc.potentialvoter.entities.PotentialVoterData;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface PotentialVoterRepositoryMapper {
    PotentialVoterData toPotentialVoterData(PotentialVoter potentialVoter);

    PotentialVoter toPotentialVoter(PotentialVoterData data);
}
