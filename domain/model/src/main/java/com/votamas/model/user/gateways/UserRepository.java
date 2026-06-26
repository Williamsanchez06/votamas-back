package com.votamas.model.user.gateways;

import com.votamas.model.user.User;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import java.util.UUID;

public interface UserRepository {

    Mono<User> findById(UUID id);

    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<User> save(User user);

    Flux<User> findAll();

    Mono<User> disable(UUID id);

    Mono<User> enable(UUID id);

}
