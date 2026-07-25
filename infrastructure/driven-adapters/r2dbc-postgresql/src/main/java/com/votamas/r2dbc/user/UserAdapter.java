package com.votamas.r2dbc.user;

import com.votamas.model.user.User;
import com.votamas.model.common.pagination.PageQuery;
import com.votamas.model.common.pagination.PageResult;
import com.votamas.model.user.gateways.UserRepository;
import com.votamas.model.exception.ConflictException;
import com.votamas.model.exception.MessageError;
import com.votamas.r2dbc.user.entities.UserData;
import com.votamas.r2dbc.user.mapper.UserRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import org.springframework.transaction.reactive.TransactionalOperator;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserAdapter implements UserRepository {

    private static final String LEADER_ROLE = "LIDER";
    private static final String ASSIGN_ROLE = """
            INSERT INTO user_role (user_id, role_id)
            SELECT :userId, role_id
              FROM role
             WHERE name = :roleName
               AND is_active = TRUE
            ON CONFLICT DO NOTHING
            """;

    private final UserReactiveRepository repository;
    private final UserRepositoryMapper mapper;
    private final R2dbcEntityTemplate template;
    private final DatabaseClient databaseClient;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<User> save(User user) {
        return repository.save(mapper.toUserData(user))
                .onErrorMap(DuplicateKeyException.class,
                        error -> new ConflictException(MessageError.EMAIL_ALREADY_REGISTERED))
                .map(mapper::toUser);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toUser);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<Boolean> isActiveById(UUID id) {
        return repository.existsByIdAndActiveTrue(id);
    }

    @Override
    public Mono<PageResult<User>> findAll(PageQuery pagination) {
        Query pageQuery = Query.empty()
                .sort(Sort.by(Sort.Direction.ASC, "name", "surname", "id"))
                .limit(pagination.size())
                .offset(pagination.offset());

        return Mono.zip(
                template.select(pageQuery, UserData.class).map(mapper::toUser).collectList(),
                template.count(Query.empty(), UserData.class),
                (users, total) -> PageResult.of(users, pagination, total)
        );
    }

    @Override
    public Mono<User> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toUser);
    }

    @Override
    public Mono<User> updateStatus(UUID id, boolean active) {
        return repository.findById(id)
                .flatMap(existing -> {
                    var updated = existing.toBuilder()
                            .active(active)
                            .build();
                    return repository.save(updated);
                })
                .map(mapper::toUser);
    }

    @Override
    public Mono<User> saveAsLeader(User user) {
        return repository.save(mapper.toUserData(user))
                .flatMap(saved -> databaseClient.sql(ASSIGN_ROLE)
                        .bind("userId", saved.id())
                        .bind("roleName", LEADER_ROLE)
                        .fetch()
                        .rowsUpdated()
                        .filter(rows -> rows > 0)
                        .switchIfEmpty(Mono.error(new IllegalStateException(
                                "El rol LIDER no está configurado o se encuentra inactivo")))
                        .thenReturn(saved))
                .map(mapper::toUser)
                .onErrorMap(DuplicateKeyException.class,
                        error -> new ConflictException(MessageError.EMAIL_ALREADY_REGISTERED))
                .as(transactionalOperator::transactional);
    }

}

