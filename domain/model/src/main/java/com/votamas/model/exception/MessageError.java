package com.votamas.model.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageError {
    VALIDATION_ERROR(Constants.ERROR_400, "La solicitud contiene datos inválidos"),
    AUTHENTICATION_REQUIRED(Constants.ERROR_401, "Se requiere autenticación"),
    INVALID_CREDENTIALS(Constants.ERROR_401, "Credenciales inválidas"),
    ACCESS_DENIED(Constants.ERROR_403, "No tiene permisos para realizar esta operación"),
    NO_USER_FOUND(Constants.ERROR_404, "Usuario no encontrado"),
    EMAIL_ALREADY_REGISTERED(Constants.ERROR_409, "El email ya está registrado");

    private final String code;
    private final String message;

    private static class Constants {
        public static final String ERROR_400 = "BP400";
        public static final String ERROR_401 = "BP401";
        public static final String ERROR_403 = "BP403";
        public static final String ERROR_404 = "BP404";
        public static final String ERROR_409 = "BP409";
    }

}
