package com.votamas.model.auth;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserPermission(
        String role,
        String module,
        String permissions
) {
}
