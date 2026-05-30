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
                    m.name AS module,
                    string_agg(p.name, ', ' ORDER BY p.name) AS permissions
                FROM vota_mas.users u
                INNER JOIN vota_mas.user_role ur ON u.user_id = ur.user_id
                INNER JOIN vota_mas.role r ON ur.role_id = r.role_id
                INNER JOIN vota_mas.role_permission rp ON r.role_id = rp.role_id
                INNER JOIN vota_mas.permission p ON rp.permission_id = p.permission_id
                INNER JOIN vota_mas.module m ON p.module_id = m.module_id
                WHERE u.user_id = :userId
                GROUP BY r.name, m.name
                """)
                .bind("userId", userId)
                .map((row, metadata) -> UserPermission.builder()
                        .role(row.get("role", String.class))
                        .module(row.get("module", String.class))
                        .permissions(row.get("permissions", String.class))
                        .build())
                .all();
    }

}
