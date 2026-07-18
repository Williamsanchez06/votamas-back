package com.votamas.r2dbc.user;

import com.votamas.model.user.User;
import com.votamas.model.common.pagination.PageRequest;
import com.votamas.model.common.pagination.PageResult;
import com.votamas.model.user.gateways.UserRepository;
import com.votamas.r2dbc.user.entities.UserData;
import com.votamas.r2dbc.user.mapper.UserRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
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
    public Mono<PageResult<User>> findAll(PageRequest pageRequest) {
        Query pageQuery = Query.empty()
                .sort(Sort.by(Sort.Direction.ASC, "name", "surname"))
                .limit(pageRequest.size())
                .offset(pageRequest.offset());

        return Mono.zip(
                template.select(pageQuery, UserData.class).map(mapper::toUser).collectList(),
                template.count(Query.empty(), UserData.class),
                (users, total) -> PageResult.of(users, pageRequest, total)
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

