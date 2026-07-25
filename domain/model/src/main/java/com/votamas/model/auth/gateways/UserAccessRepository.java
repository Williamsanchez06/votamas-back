package com.votamas.model.auth.gateways;

import com.votamas.model.auth.CurrentUserProfile;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserAccessRepository {
    Mono<CurrentUserProfile> findByUserId(UUID userId);
}
