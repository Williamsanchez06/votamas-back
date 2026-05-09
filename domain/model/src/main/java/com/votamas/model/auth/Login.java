package com.votamas.model.auth;

import lombok.Builder;

@Builder
public record Login(
        String email, String password
) {
}
