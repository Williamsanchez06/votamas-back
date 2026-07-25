package com.votamas.model.auth.gateways;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRoleRepository {
    Mono<Boolean> hasActiveRole(UUID userId, String roleName);
}
