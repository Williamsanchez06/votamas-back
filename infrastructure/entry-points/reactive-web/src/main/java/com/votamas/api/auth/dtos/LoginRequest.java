package com.votamas.api.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El correo electrónico no tiene un formato válido")
        @Size(max = 100, message = "El correo electrónico no puede superar 100 caracteres")
        String email,
        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
    public LoginRequest {
        email = email == null ? null : email.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
