package com.votamas.model.auth;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        String email,
        Set<String> roles,
        Set<String> authorities
) {}
