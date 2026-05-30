package com.votamas.model.auth;

import lombok.Builder;

@Builder(toBuilder = true)
public record Token(String accessToken) {
}
