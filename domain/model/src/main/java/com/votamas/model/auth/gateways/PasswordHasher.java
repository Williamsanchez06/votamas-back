package com.votamas.model.auth.gateways;

import reactor.core.publisher.Mono;

public interface PasswordHasher {

    Mono<String> hash(String password);

    Mono<Boolean> matches(String rawPassword, String encodedPassword);
}
