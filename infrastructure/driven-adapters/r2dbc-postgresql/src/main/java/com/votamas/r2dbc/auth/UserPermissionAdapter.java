package com.votamas.r2dbc.auth;

import com.votamas.model.auth.UserPermission;
import com.votamas.model.auth.gateways.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserPermissionAdapter implements UserPermissionRepository {

    private final DatabaseClient databaseClient;

    @Override
    public Flux<UserPermission> findPermissionsByUserId(UUID userId) {
        return databaseClient.sql("""
                SELECT
                    r.name AS role,
                    p.name AS permission
                FROM users u
                INNER JOIN user_role ur ON u.user_id = ur.user_id
                INNER JOIN role r ON ur.role_id = r.role_id
                INNER JOIN role_permission rp ON r.role_id = rp.role_id
                INNER JOIN permission p ON rp.permission_id = p.permission_id
                WHERE u.user_id = :userId
                  AND u.active = TRUE
                  AND r.is_active = TRUE
                ORDER BY r.name, p.name
                """)
                .bind("userId", userId)
                .map((row, metadata) -> UserPermission.builder()
                        .role(row.get("role", String.class))
                        .permission(row.get("permission", String.class))
                        .build())
                .all();
    }

}
