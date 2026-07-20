package com.votamas.api.utils;

import com.votamas.model.exception.MessageError;
import com.votamas.model.exception.ValidationException;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class PathVariableParser {
    public static UUID uuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new ValidationException(MessageError.VALIDATION_ERROR);
        }
    }
}
