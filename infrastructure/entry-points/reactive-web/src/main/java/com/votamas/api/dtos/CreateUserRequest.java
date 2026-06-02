package com.votamas.api.dtos;

public record CreateUserRequest(
        String name,
        String surname,
        String email,
        String password
) {}