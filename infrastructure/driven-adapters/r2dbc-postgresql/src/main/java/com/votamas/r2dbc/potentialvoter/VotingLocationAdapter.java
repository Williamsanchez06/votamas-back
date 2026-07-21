package com.votamas.r2dbc.potentialvoter;

import com.votamas.model.potentialvoter.VotingZone;
import com.votamas.model.potentialvoter.VotingZoneDetails;
import com.votamas.model.potentialvoter.VotingTableReference;
import com.votamas.model.potentialvoter.gateways.VotingLocationRepository;
import com.votamas.r2dbc.potentialvoter.mapper.VotingLocationDetailsMapper;
import com.votamas.r2dbc.potentialvoter.projections.VotingLocationProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VotingLocationAdapter implements VotingLocationRepository {

    private static final String FIND_ALL_ZONES = """
            SELECT voting_zone_id AS id, name
              FROM voting_zones
             ORDER BY name, voting_zone_id
            """;

    private static final String FIND_ZONE_DETAILS = """
            SELECT vz.voting_zone_id,
                   vz.name AS voting_zone_name,
                   pp.polling_place_id,
                   pp.name AS polling_place_name,
                   vt.voting_table_id,
                   vt.table_number
              FROM voting_zones vz
              LEFT JOIN polling_places pp
                     ON pp.voting_zone_id = vz.voting_zone_id
              LEFT JOIN voting_tables vt
                     ON vt.polling_place_id = pp.polling_place_id
             WHERE vz.voting_zone_id = :zoneId
             ORDER BY pp.name, pp.polling_place_id, vt.table_number, vt.voting_table_id
            """;

    private static final String EXISTS_VOTING_TABLE = """
            SELECT EXISTS (
                SELECT 1
                  FROM voting_tables
                 WHERE voting_table_id = :votingTableId
            ) AS found
            """;

    private static final String FIND_ALL_VOTING_TABLE_REFERENCES = """
            SELECT vt.voting_table_id AS id,
                   vz.name AS voting_zone_name,
                   pp.name AS polling_place_name,
                   vt.table_number
              FROM voting_tables vt
              INNER JOIN polling_places pp
                      ON pp.polling_place_id = vt.polling_place_id
              INNER JOIN voting_zones vz
                      ON vz.voting_zone_id = pp.voting_zone_id
             ORDER BY vz.name, pp.name, vt.table_number
            """;

    private final DatabaseClient databaseClient;
    private final VotingLocationDetailsMapper mapper;

    @Override
    public Flux<VotingZone> findAllZones() {
        return databaseClient.sql(FIND_ALL_ZONES)
                .mapProperties(VotingZone.class)
                .all();
    }

    @Override
    public Mono<VotingZoneDetails> findZoneDetailsById(UUID zoneId) {
        return databaseClient.sql(FIND_ZONE_DETAILS)
                .bind("zoneId", zoneId)
                .mapProperties(VotingLocationProjection.class)
                .all()
                .collectList()
                .filter(rows -> !rows.isEmpty())
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsVotingTableById(UUID votingTableId) {
        return databaseClient.sql(EXISTS_VOTING_TABLE)
                .bind("votingTableId", votingTableId)
                .map((row, metadata) -> Boolean.TRUE.equals(row.get("found", Boolean.class)))
                .one();
    }

    @Override
    public Flux<VotingTableReference> findAllVotingTableReferences() {
        return databaseClient.sql(FIND_ALL_VOTING_TABLE_REFERENCES)
                .mapProperties(VotingTableReference.class)
                .all();
    }
}
