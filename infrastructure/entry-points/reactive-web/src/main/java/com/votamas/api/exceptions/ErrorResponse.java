package com.votamas.api.exceptions;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record ErrorResponse(String code, String message, int status, String requestId, Instant timestamp) {
}
