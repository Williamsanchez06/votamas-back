package com.votamas.r2dbc.potentialvoter;

import com.votamas.model.common.pagination.PageQuery;
import com.votamas.model.common.pagination.PageResult;
import com.votamas.model.potentialvoter.PotentialVoterDetails;
import com.votamas.model.potentialvoter.PotentialVoterExportRow;
import com.votamas.model.potentialvoter.PotentialVoterSearchCriteria;
import com.votamas.model.potentialvoter.gateways.PotentialVoterQueryRepository;
import com.votamas.r2dbc.potentialvoter.mapper.PotentialVoterDetailsMapper;
import com.votamas.r2dbc.potentialvoter.projections.PotentialVoterDetailsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PotentialVoterQueryAdapter implements PotentialVoterQueryRepository {

    private static final String SELECT_COLUMNS = """
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
            """;

    private static final String COUNT = """
            SELECT COUNT(*) AS total_elements
            """;

    private static final String EXPORT_COLUMNS = """
            SELECT pv.identification,
                   pv.first_name,
                   pv.last_name,
                   vz.name AS voting_zone_name,
                   pp.name AS polling_place_name,
                   vt.table_number,
                   pv.registration_date,
                   CONCAT(leader.name, ' ', leader.surname) AS assigned_leader_name
            """;

    private static final String FROM_WITH_VOTING_LOCATION = """
              FROM potential_voters pv
              INNER JOIN voting_tables vt
                      ON vt.voting_table_id = pv.voting_table_id
              INNER JOIN polling_places pp
                      ON pp.polling_place_id = vt.polling_place_id
              INNER JOIN voting_zones vz
                      ON vz.voting_zone_id = pp.voting_zone_id
            """;

    private static final String PAGE_ORDER = """
             ORDER BY pv.potential_voter_id
             LIMIT :limit OFFSET :offset
            """;

    private static final String LEADER_JOIN = """
              INNER JOIN users leader
                      ON leader.user_id = pv.assigned_leader_id
            """;

    private static final String EXPORT_ORDER = """
             ORDER BY pv.potential_voter_id
             LIMIT :limit
            """;

    private static final String IDENTIFICATION_FILTER =
            "STRPOS(pv.identification, :identification) > 0";
    private static final String POLLING_PLACE_FILTER =
            "pp.polling_place_id = :pollingPlaceId";
    private static final String VOTING_ZONE_FILTER =
            "vz.voting_zone_id = :votingZoneId";
    private static final String ASSIGNED_LEADER_FILTER =
            "pv.assigned_leader_id = :assignedLeaderId";

    private final DatabaseClient databaseClient;
    private final PotentialVoterDetailsMapper mapper;

    @Override
    public Mono<PageResult<PotentialVoterDetails>> findAllWithVotingLocation(
            PotentialVoterSearchCriteria criteria) {
        FilterQuery filter = buildFilterQuery(criteria);
        PageQuery pagination = criteria.pagination();

        Mono<List<PotentialVoterDetails>> content = filter.bind(databaseClient.sql(
                        SELECT_COLUMNS + FROM_WITH_VOTING_LOCATION + filter.whereClause() + PAGE_ORDER))
                .bind("limit", pagination.size())
                .bind("offset", pagination.offset())
                .mapProperties(PotentialVoterDetailsProjection.class)
                .all()
                .map(mapper::toDomain)
                .collectList();

        Mono<Long> total = filter.bind(databaseClient.sql(
                        COUNT + FROM_WITH_VOTING_LOCATION + filter.whereClause()))
                .map((row, metadata) -> row.get("total_elements", Long.class))
                .one()
                .defaultIfEmpty(0L);

        return Mono.zip(content, total,
                (voters, totalElements) -> PageResult.of(voters, pagination, totalElements));
    }

    @Override
    public Flux<PotentialVoterExportRow> findForExport(PotentialVoterSearchCriteria criteria, int limit) {
        FilterQuery filter = buildFilterQuery(criteria);
        return filter.bind(databaseClient.sql(
                        EXPORT_COLUMNS + FROM_WITH_VOTING_LOCATION + LEADER_JOIN
                                + filter.whereClause() + EXPORT_ORDER))
                .bind("limit", limit)
                .mapProperties(PotentialVoterExportRow.class)
                .all();
    }

    private FilterQuery buildFilterQuery(PotentialVoterSearchCriteria criteria) {
        List<String> conditions = new ArrayList<>(4);
        Map<String, Object> parameters = new LinkedHashMap<>(4);

        addFilter(conditions, parameters, IDENTIFICATION_FILTER,
                "identification", criteria.identification());
        addFilter(conditions, parameters, POLLING_PLACE_FILTER,
                "pollingPlaceId", criteria.pollingPlaceId());
        addFilter(conditions, parameters, VOTING_ZONE_FILTER,
                "votingZoneId", criteria.votingZoneId());
        addFilter(conditions, parameters, ASSIGNED_LEADER_FILTER,
                "assignedLeaderId", criteria.assignedLeaderId());

        String whereClause = conditions.isEmpty()
                ? ""
                : " WHERE " + String.join(" AND ", conditions) + System.lineSeparator();
        return new FilterQuery(whereClause, Map.copyOf(parameters));
    }

    private void addFilter(List<String> conditions, Map<String, Object> parameters,
                           String condition, String parameterName, Object value) {
        if (value != null) {
            conditions.add(condition);
            parameters.put(parameterName, value);
        }
    }

    private record FilterQuery(String whereClause, Map<String, Object> parameters) {
        private DatabaseClient.GenericExecuteSpec bind(DatabaseClient.GenericExecuteSpec spec) {
            return spec.bindValues(parameters);
        }
    }
}
