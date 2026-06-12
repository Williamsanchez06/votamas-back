package com.votamas.api.user.dtos;

public record UserRequestDTO(
        String name,
        String surname,
        String email,
        String password
) {}