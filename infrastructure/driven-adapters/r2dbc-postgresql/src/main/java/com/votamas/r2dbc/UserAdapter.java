package com.votamas.r2dbc;

import com.votamas.model.user.gateways.User;
import com.votamas.model.user.gateways.UserRepository;
import com.votamas.r2dbc.mapper.UserRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserAdapter implements UserRepository {

    private final UserReactiveRepository repository;
    private final UserRepositoryMapper mapper;

    @Override
    public Mono<User> save(User user) {
        System.out.println(">>> INTENTANDO GUARDAR: " + user.email());
        return repository.save(mapper.toUserData(user))
                .doOnSuccess(saved -> System.out.println(">>> GUARDADO EN BD: " + saved))
                .doOnError(error -> System.out.println(">>> ERROR AL GUARDAR: " + error.getMessage()))
                .map(mapper::toUser);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toUser);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.findByEmail(email)
                .map(userData -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Flux<User> findAll() {
        return repository.findAll().map(mapper::toUser);
    }

}