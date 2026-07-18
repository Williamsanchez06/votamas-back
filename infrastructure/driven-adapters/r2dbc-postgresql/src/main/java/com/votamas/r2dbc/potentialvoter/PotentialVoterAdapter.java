package com.votamas.r2dbc.potentialvoter;

import com.votamas.model.common.pagination.PageRequest;
import com.votamas.model.common.pagination.PageResult;
import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.model.potentialvoter.gateways.PotentialVoterRepository;
import com.votamas.r2dbc.potentialvoter.entities.PotentialVoterData;
import com.votamas.r2dbc.potentialvoter.mapper.PotentialVoterRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PotentialVoterAdapter implements PotentialVoterRepository {

    private final PotentialVoterReactiveRepository repository;
    private final PotentialVoterRepositoryMapper mapper;
    private final R2dbcEntityTemplate template;

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
    public Mono<PageResult<PotentialVoter>> findAll(PageRequest pageRequest) {
        Query pageQuery = Query.empty()
                .limit(pageRequest.size())
                .offset(pageRequest.offset());

        return Mono.zip(
                template.select(pageQuery, PotentialVoterData.class).map(mapper::toPotentialVoter).collectList(),
                template.count(Query.empty(), PotentialVoterData.class),
                (users, total) -> PageResult.of(users, pageRequest, total)
        );
    }

    @Override
    public Mono<PotentialVoter> findById(UUID id) {
        return repository.findById(id).map(mapper::toPotentialVoter);
    }
}
