package com.votamas.model.auth.gateways;

import com.votamas.model.auth.UserPermission;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface UserPermissionRepository {


    Flux<UserPermission> findPermissionsByUserId(UUID userId);

}
