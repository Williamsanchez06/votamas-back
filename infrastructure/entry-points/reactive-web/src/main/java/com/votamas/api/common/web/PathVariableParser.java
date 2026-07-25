package com.votamas.api.common.web;

import com.votamas.api.common.validation.InvalidRequestException;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class PathVariableParser {
    public static UUID uuid(String value, String field) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw InvalidRequestException.forField(
                    field, "El identificador no tiene un formato UUID válido");
        }
    }
}
