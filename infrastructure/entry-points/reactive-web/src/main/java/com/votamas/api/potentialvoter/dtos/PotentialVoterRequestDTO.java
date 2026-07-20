package com.votamas.api.potentialvoter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record PotentialVoterRequestDTO(
        @NotBlank(message = "La identificación es obligatoria")
        @Size(max = 30, message = "La identificación no puede superar 30 caracteres")
        String identification,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        String firstName,
        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 150, message = "El apellido no puede superar 150 caracteres")
        String lastName,
        @NotNull(message = "La mesa de votación es obligatoria")
        UUID votingTableId,
        @NotNull(message = "El líder asignado es obligatorio")
        UUID assignedLeaderId
) {
    public PotentialVoterRequestDTO {
        identification = trim(identification);
        firstName = trim(firstName);
        lastName = trim(lastName);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
