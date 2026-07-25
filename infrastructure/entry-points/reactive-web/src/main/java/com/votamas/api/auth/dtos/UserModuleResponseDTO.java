package com.votamas.api.auth.dtos;

import java.util.List;
import java.util.UUID;

public record UserModuleResponseDTO(
        UUID id,
        UUID parentId,
        int order,
        String name,
        String route,
        String description,
        String primaryIcon,
        String secondaryIcon,
        String tertiaryIcon,
        List<String> permissions
) {
}
