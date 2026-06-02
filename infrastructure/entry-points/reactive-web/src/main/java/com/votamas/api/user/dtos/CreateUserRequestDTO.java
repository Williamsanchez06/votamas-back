package com.votamas.api.user.dtos;

public record CreateUserRequestDTO(
        String name,
        String surname,
        String email,
        String password
) {}