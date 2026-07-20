package com.votamas.api.exceptions;

import lombok.Builder;
import com.votamas.api.common.validation.FieldValidationError;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record ErrorResponse(String code, String message, int status, String requestId, Instant timestamp,
                            @JsonInclude(JsonInclude.Include.NON_EMPTY) List<FieldValidationError> errors) {
    public ErrorResponse(String code, String message, int status, String requestId, Instant timestamp) {
        this(code, message, status, requestId, timestamp, null);
    }
}
