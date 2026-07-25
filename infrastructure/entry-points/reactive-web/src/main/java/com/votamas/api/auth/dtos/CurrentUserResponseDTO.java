package com.votamas.api.auth.dtos;

import java.util.List;
import java.util.UUID;

public record CurrentUserResponseDTO(
        UUID id,
        String name,
        String surname,
        String email,
        Boolean active,
        List<String> roles,
        List<UserModuleResponseDTO> modules
) {
}
