package com.votamas.r2dbc.auth;

import com.votamas.model.auth.CurrentUserProfile;
import com.votamas.model.auth.UserModule;
import com.votamas.model.auth.gateways.UserAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserAccessAdapter implements UserAccessRepository {

    private static final String FIND_USER_ACCESS = """
            SELECT u.user_id,
                   u.name,
                   u.surname,
                   u.email,
                   u.active,
                   r.name AS role_name,
                   m.module_id,
                   m.parent_id,
                   m.module_order,
                   m.name AS module_name,
                   m.route AS module_route,
                   m.description AS module_description,
                   m.primary_icon,
                   m.secondary_icon,
                   m.tertiary_icon,
                   p.name AS permission_name
              FROM users u
              LEFT JOIN user_role ur
                     ON ur.user_id = u.user_id
              LEFT JOIN role r
                     ON r.role_id = ur.role_id
                    AND r.is_active = TRUE
              LEFT JOIN role_permission rp
                     ON rp.role_id = r.role_id
              LEFT JOIN permission p
                     ON p.permission_id = rp.permission_id
              LEFT JOIN module m
                     ON m.module_id = p.module_id
             WHERE u.user_id = :userId
               AND u.active = TRUE
             ORDER BY m.module_order, m.module_id, p.name
            """;

    private final DatabaseClient databaseClient;

    @Override
    public Mono<CurrentUserProfile> findByUserId(UUID userId) {
        return databaseClient.sql(FIND_USER_ACCESS)
                .bind("userId", userId)
                .mapProperties(UserAccessRow.class)
                .all()
                .collectList()
                .filter(rows -> !rows.isEmpty())
                .map(this::toProfile);
    }

    private CurrentUserProfile toProfile(List<UserAccessRow> rows) {
        UserAccessRow user = rows.getFirst();
        var roles = new LinkedHashSet<String>();
        Map<UUID, ModuleAccumulator> modules = new LinkedHashMap<>();

        for (UserAccessRow row : rows) {
            if (row.roleName() != null) {
                roles.add(row.roleName());
            }
            if (row.moduleId() != null) {
                modules.computeIfAbsent(row.moduleId(), ignored -> new ModuleAccumulator(row))
                        .addPermission(row.permissionName());
            }
        }

        return new CurrentUserProfile(
                user.userId(),
                user.name(),
                user.surname(),
                user.email(),
                user.active(),
                List.copyOf(roles),
                modules.values().stream().map(ModuleAccumulator::toDomain).toList()
        );
    }

    private record UserAccessRow(
            UUID userId,
            String name,
            String surname,
            String email,
            Boolean active,
            String roleName,
            UUID moduleId,
            UUID parentId,
            Integer moduleOrder,
            String moduleName,
            String moduleRoute,
            String moduleDescription,
            String primaryIcon,
            String secondaryIcon,
            String tertiaryIcon,
            String permissionName
    ) {
    }

    private static final class ModuleAccumulator {
        private final UserAccessRow module;
        private final LinkedHashSet<String> permissions = new LinkedHashSet<>();

        private ModuleAccumulator(UserAccessRow module) {
            this.module = module;
        }

        private ModuleAccumulator addPermission(String permission) {
            if (permission != null) {
                permissions.add(permission);
            }
            return this;
        }

        private UserModule toDomain() {
            return new UserModule(
                    module.moduleId(),
                    module.parentId(),
                    module.moduleOrder(),
                    module.moduleName(),
                    module.moduleRoute(),
                    module.moduleDescription(),
                    module.primaryIcon(),
                    module.secondaryIcon(),
                    module.tertiaryIcon(),
                    new ArrayList<>(permissions)
            );
        }
    }
}
