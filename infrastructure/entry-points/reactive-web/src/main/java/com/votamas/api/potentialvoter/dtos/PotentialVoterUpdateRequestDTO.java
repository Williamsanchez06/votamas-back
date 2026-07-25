package com.votamas.api.potentialvoter.dtos;

import com.votamas.api.common.validation.RequestFieldNormalizer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record PotentialVoterUpdateRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        String firstName,
        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 150, message = "El apellido no puede superar 150 caracteres")
        String lastName,
        @NotNull(message = "La mesa de votación es obligatoria")
        UUID votingTableId
) {
    public PotentialVoterUpdateRequestDTO {
        firstName = RequestFieldNormalizer.normalizeText(firstName);
        lastName = RequestFieldNormalizer.normalizeText(lastName);
    }
}
