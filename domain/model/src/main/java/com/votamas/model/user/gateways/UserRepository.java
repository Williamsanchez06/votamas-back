package com.votamas.model.user.gateways;

import com.votamas.model.user.gateways.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);
    Mono<User> save(User user);
}