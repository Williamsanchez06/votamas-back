package com.votamas.api.auth.dtos;

public record LoginRequest(
        String email, String password
) {
}
