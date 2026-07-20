package com.votamas.r2dbc.potentialvoter;

import com.votamas.model.common.pagination.PageRequest;
import com.votamas.model.common.pagination.PageResult;
import com.votamas.model.potentialvoter.PotentialVoterDetails;
import com.votamas.model.potentialvoter.gateways.PotentialVoterQueryRepository;
import com.votamas.r2dbc.potentialvoter.entities.PotentialVoterData;
import com.votamas.r2dbc.potentialvoter.mapper.PotentialVoterDetailsMapper;
import com.votamas.r2dbc.potentialvoter.projections.PotentialVoterDetailsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class PotentialVoterQueryAdapter implements PotentialVoterQueryRepository {

    private static final String FIND_PAGE_WITH_VOTING_LOCATION = """
            SELECT pv.potential_voter_id,
                   pv.identification,
                   pv.first_name,
                   pv.last_name,
                   pv.registration_date,
                   pv.assigned_leader_id,
                   vt.voting_table_id,
                   vt.table_number,
                   pp.polling_place_id,
                   pp.name AS polling_place_name,
                   vz.voting_zone_id,
                   vz.name AS voting_zone_name
              FROM vota_mas.potential_voters pv
              LEFT JOIN vota_mas.voting_tables vt
                     ON vt.voting_table_id = pv.voting_table_id
              LEFT JOIN vota_mas.polling_places pp
                     ON pp.polling_place_id = vt.polling_place_id
              LEFT JOIN vota_mas.voting_zones vz
                     ON vz.voting_zone_id = pp.voting_zone_id
             ORDER BY pv.potential_voter_id
             LIMIT :limit OFFSET :offset
            """;

    private final DatabaseClient databaseClient;
    private final R2dbcEntityTemplate template;
    private final PotentialVoterDetailsMapper mapper;

    @Override
    public Mono<PageResult<PotentialVoterDetails>> findAllWithVotingLocation(PageRequest pageRequest) {
        Mono<java.util.List<PotentialVoterDetails>> content = databaseClient
                .sql(FIND_PAGE_WITH_VOTING_LOCATION)
                .bind("limit", pageRequest.size())
                .bind("offset", pageRequest.offset())
                .mapProperties(PotentialVoterDetailsProjection.class)
                .all()
                .map(mapper::toDomain)
                .collectList();

        Mono<Long> total = template.count(Query.empty(), PotentialVoterData.class);

        return Mono.zip(content, total,
                (voters, totalElements) -> PageResult.of(voters, pageRequest, totalElements));
    }
}
