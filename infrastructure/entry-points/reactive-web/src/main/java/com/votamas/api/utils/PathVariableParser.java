package com.votamas.api.utils;

import com.votamas.api.validation.FieldValidationError;
import com.votamas.api.validation.InvalidRequestException;
import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.List;

@UtilityClass
public class PathVariableParser {
    public static UUID uuid(String value, String field) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new InvalidRequestException(List.of(
                    new FieldValidationError(field, "El identificador no tiene un formato UUID válido")));
        }
    }
}
