package com.votamas.r2dbc.potentialvoter;

import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.model.potentialvoter.gateways.PotentialVoterRepository;
import com.votamas.model.exception.ConflictException;
import com.votamas.model.exception.MessageError;
import com.votamas.r2dbc.potentialvoter.mapper.PotentialVoterRepositoryMapper;
import com.votamas.r2dbc.potentialvoter.entities.PotentialVoterData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PotentialVoterAdapter implements PotentialVoterRepository {

    private final PotentialVoterReactiveRepository repository;
    private final PotentialVoterRepositoryMapper mapper;

    @Override
    public Mono<PotentialVoter> save(PotentialVoter potentialVoter) {
        return repository.save(mapper.toPotentialVoterData(potentialVoter))
                .onErrorMap(DuplicateKeyException.class,
                        error -> new ConflictException(MessageError.ID_ALREADY_REGISTERED))
                .map(mapper::toPotentialVoter);
    }

    @Override
    public Mono<Boolean> existsByIdentification(String identification) {
        return repository.existsByIdentification(identification);
    }

    @Override
    public Flux<String> findExistingIdentifications(Set<String> identifications) {
        if (identifications.isEmpty()) {
            return Flux.empty();
        }
        return repository.findAllByIdentificationIn(identifications)
                .map(PotentialVoterData::identification);
    }

    @Override
    public Mono<PotentialVoter> findById(UUID id) {
        return repository.findById(id).map(mapper::toPotentialVoter);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}
