package com.votamas.api.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String name,
        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100, message = "El apellido no puede superar 100 caracteres")
        String surname,
        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El correo electrónico no tiene un formato válido")
        @Size(max = 100, message = "El correo electrónico no puede superar 100 caracteres")
        String email
) {
    public UserUpdateRequestDTO {
        name = normalizeText(name);
        surname = normalizeText(surname);
        email = email == null ? null : email.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private static String normalizeText(String value) {
        return value == null ? null : value.trim().replaceAll("\\s+", " ");
    }
}
