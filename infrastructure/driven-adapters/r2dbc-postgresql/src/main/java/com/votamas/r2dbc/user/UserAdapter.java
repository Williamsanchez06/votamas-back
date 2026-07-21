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
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserAdapter implements UserRepository {

    private final UserReactiveRepository repository;
    private final UserRepositoryMapper mapper;
    private final R2dbcEntityTemplate template;

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

}

