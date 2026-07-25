package com.votamas.api.common.validation;

import java.util.List;

public class InvalidRequestException extends RuntimeException {
    private final List<FieldValidationError> errors;

    public InvalidRequestException(List<FieldValidationError> errors) {
        super("La petición no contiene datos válidos");
        this.errors = List.copyOf(errors);
    }

    public static InvalidRequestException forField(String field, String message) {
        return new InvalidRequestException(List.of(new FieldValidationError(field, message)));
    }

    public List<FieldValidationError> errors() {
        return errors;
    }
}
