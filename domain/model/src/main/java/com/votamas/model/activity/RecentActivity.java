package com.votamas.model.activity;

import java.time.LocalDateTime;
import java.util.UUID;

public record RecentActivity(
        UUID referenceId,
        String entity,
        String action,
        String description,
        LocalDateTime occurredAt
) {
}
