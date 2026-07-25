package com.votamas.model.auth;

import java.util.List;
import java.util.UUID;

public record CurrentUserProfile(
        UUID id,
        String name,
        String surname,
        String email,
        Boolean active,
        List<String> roles,
        List<UserModule> modules
) {
    public CurrentUserProfile {
        roles = List.copyOf(roles);
        modules = List.copyOf(modules);
    }
}
