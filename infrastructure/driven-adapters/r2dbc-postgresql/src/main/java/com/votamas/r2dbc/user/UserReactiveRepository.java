package com.votamas.r2dbc.user;

import com.votamas.r2dbc.user.entities.UserData;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserData, UUID> {
    Mono<UserData> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);
}
