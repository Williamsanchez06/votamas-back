package com.votamas.api.exceptions;

record ApiError(String code, String message, int status) {
    static final ApiError INVALID_REQUEST = new ApiError("BP400", "La solicitud contiene datos inválidos", 400);
    static final ApiError RESOURCE_NOT_FOUND = new ApiError("BP404", "Recurso no encontrado", 404);
    static final ApiError INTERNAL_ERROR = new ApiError("BP500", "Ocurrió un error interno", 500);
}
