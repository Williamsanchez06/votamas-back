package com.votamas.r2dbc.auth;

import com.votamas.model.auth.gateways.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRoleAdapter implements UserRoleRepository {

    private static final String HAS_ACTIVE_ROLE = """
            SELECT EXISTS (
                SELECT 1
                  FROM users u
                  JOIN user_role ur
                    ON ur.user_id = u.user_id
                  JOIN role r
                    ON r.role_id = ur.role_id
                 WHERE u.user_id = :userId
                   AND u.active = TRUE
                   AND r.name = :roleName
                   AND r.is_active = TRUE
            ) AS has_role
            """;

    private final DatabaseClient databaseClient;

    @Override
    public Mono<Boolean> hasActiveRole(UUID userId, String roleName) {
        return databaseClient.sql(HAS_ACTIVE_ROLE)
                .bind("userId", userId)
                .bind("roleName", roleName)
                .map((row, metadata) -> Boolean.TRUE.equals(row.get("has_role", Boolean.class)))
                .one()
                .defaultIfEmpty(false);
    }
}
