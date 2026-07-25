package com.votamas.model.auth;

import java.util.List;
import java.util.UUID;

public record UserModule(
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
    public UserModule {
        permissions = List.copyOf(permissions);
    }
}
