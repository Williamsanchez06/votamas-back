package com.votamas.model.auth;

import lombok.Builder;

@Builder(toBuilder = true)
public record Login(
        String email, String password
) {
}
