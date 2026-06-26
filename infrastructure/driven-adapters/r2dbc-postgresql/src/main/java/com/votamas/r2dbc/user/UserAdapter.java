package com.votamas.r2dbc.user;

import com.votamas.model.user.User;
import com.votamas.model.user.gateways.UserRepository;
import com.votamas.r2dbc.user.mapper.UserRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserAdapter implements UserRepository {

    private final UserReactiveRepository repository;
    private final UserRepositoryMapper mapper;

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
    public Flux<User> findAll() {
        return repository.findAll().map(mapper::toUser);
    }

    @Override
    public Mono<User> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toUser);
    }

    @Override
    public Mono<User> disable(UUID id) {
        return repository.findById(id)
                .flatMap(existing -> {
                    var updated = existing.toBuilder()
                            .active(false)
                            .build();
                    return repository.save(updated);
                })
                .map(mapper::toUser);
    }

    @Override
    public Mono<User> enable(UUID id) {
        return repository.findById(id)
                .flatMap(existing -> {
                    var updated = existing.toBuilder()
                            .active(true)
                            .build();
                    return repository.save(updated);
                })
                .map(mapper::toUser);
    }
}

