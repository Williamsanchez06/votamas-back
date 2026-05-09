package com.votamas.api.dtos;

public record LoginRequest(
        String email, String password
) {
}
