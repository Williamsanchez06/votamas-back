package com.votamas.r2dbc.user;

import com.votamas.r2dbc.user.entities.UserData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserData, UUID> {
    Mono<UserData> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByIdAndActiveTrue(UUID id);

    @Query("""
            SELECT u.*
              FROM users u
             WHERE NOT EXISTS (
                       SELECT 1
                         FROM user_role ur
                         JOIN role r
                           ON r.role_id = ur.role_id
                        WHERE ur.user_id = u.user_id
                          AND r.name = :excludedRole
                   )
             ORDER BY u.name, u.surname, u.user_id
             LIMIT :limit
            OFFSET :offset
            """)
    Flux<UserData> findAllExcludingRole(
            @Param("excludedRole") String excludedRole,
            @Param("limit") int limit,
            @Param("offset") long offset
    );

    @Query("""
            SELECT COUNT(*)
              FROM users u
             WHERE NOT EXISTS (
                       SELECT 1
                         FROM user_role ur
                         JOIN role r
                           ON r.role_id = ur.role_id
                        WHERE ur.user_id = u.user_id
                          AND r.name = :excludedRole
                   )
            """)
    Mono<Long> countExcludingRole(@Param("excludedRole") String excludedRole);
}
