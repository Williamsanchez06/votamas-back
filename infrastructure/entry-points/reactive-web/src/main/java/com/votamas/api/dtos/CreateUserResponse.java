package com.votamas.api.dtos;

import java.util.UUID;

public record CreateUserResponse(
        UUID id,
        String name,
        String surname,
        String email
) {}