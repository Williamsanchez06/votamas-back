package com.votamas.r2dbc.potentialvoter;

import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.model.potentialvoter.gateways.PotentialVoterRepository;
import com.votamas.r2dbc.potentialvoter.mapper.PotentialVoterRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PotentialVoterAdapter implements PotentialVoterRepository {

    private final PotentialVoterReactiveRepository repository;
    private final PotentialVoterRepositoryMapper mapper;

    @Override
    public Mono<PotentialVoter> save(PotentialVoter potentialVoter) {
        return repository.save(mapper.toPotentialVoterData(potentialVoter))
                .map(mapper::toPotentialVoter);
    }

    @Override
    public Mono<Boolean> existsByIdentification(String identification) {
        return repository.existsByIdentification(identification);
    }

    @Override
    public Mono<PotentialVoter> findById(UUID id) {
        return repository.findById(id).map(mapper::toPotentialVoter);
    }
}
