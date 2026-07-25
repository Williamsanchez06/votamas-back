package com.votamas.api.activity.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record RecentActivityResponseDTO(
        UUID referenceId,
        String entity,
        String action,
        String description,
        LocalDateTime occurredAt
) {
}
