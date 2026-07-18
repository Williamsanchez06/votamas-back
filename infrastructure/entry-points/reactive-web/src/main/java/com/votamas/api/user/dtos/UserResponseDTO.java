package com.votamas.api.user.dtos;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String surname,
        String email,
        Boolean active
) {
}
