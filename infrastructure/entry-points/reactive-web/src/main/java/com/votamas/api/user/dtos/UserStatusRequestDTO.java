package com.votamas.api.user.dtos;

import jakarta.validation.constraints.NotNull;

public record UserStatusRequestDTO(
        @NotNull(message = "El estado es obligatorio") Boolean active) {
}
