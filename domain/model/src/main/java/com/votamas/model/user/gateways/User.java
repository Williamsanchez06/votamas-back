package com.votamas.model.user.gateways;

import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record User(
        UUID id,
        String name,
        String surname,
        String email,
        String password
) {}