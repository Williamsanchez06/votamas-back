package com.votamas.api.exceptions;

import lombok.Builder;

@Builder(toBuilder = true)
public record ErrorResponse(String code, String message) {
}
