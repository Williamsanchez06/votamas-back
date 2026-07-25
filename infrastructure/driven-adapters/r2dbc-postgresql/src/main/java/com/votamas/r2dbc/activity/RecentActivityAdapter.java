package com.votamas.r2dbc.activity;

import com.votamas.model.activity.RecentActivity;
import com.votamas.model.activity.gateways.RecentActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class RecentActivityAdapter implements RecentActivityRepository {

    private static final String FIND_RECENT_ACTIVITY = """
            SELECT reference_id,
                   entity,
                   action,
                   description,
                   occurred_at
              FROM (
                    SELECT u.user_id AS reference_id,
                           'USER' AS entity,
                           CASE WHEN u.updated_at > u.created_at
                                THEN 'UPDATED' ELSE 'CREATED' END AS action,
                           CASE WHEN u.updated_at > u.created_at
                                THEN CONCAT('Usuario ', u.name, ' ', u.surname, ' fue actualizado')
                                ELSE CONCAT('Usuario ', u.name, ' ', u.surname, ' fue registrado')
                           END AS description,
                           GREATEST(u.created_at, u.updated_at) AS occurred_at
                      FROM users u

                    UNION ALL

                    SELECT pv.potential_voter_id AS reference_id,
                           'POTENTIAL_VOTER' AS entity,
                           CASE WHEN pv.updated_at > pv.created_at
                                THEN 'UPDATED' ELSE 'CREATED' END AS action,
                           CASE WHEN pv.updated_at > pv.created_at
                                THEN CONCAT(pv.first_name, ' ', pv.last_name, ' fue actualizado')
                                ELSE CONCAT(pv.first_name, ' ', pv.last_name, ' fue registrado')
                           END AS description,
                           GREATEST(pv.created_at, pv.updated_at) AS occurred_at
                      FROM potential_voters pv
                   ) activity
             ORDER BY occurred_at DESC, reference_id
             LIMIT :limit
            """;

    private final DatabaseClient databaseClient;

    @Override
    public Flux<RecentActivity> findRecent(int limit) {
        return databaseClient.sql(FIND_RECENT_ACTIVITY)
                .bind("limit", limit)
                .mapProperties(RecentActivity.class)
                .all();
    }
}
