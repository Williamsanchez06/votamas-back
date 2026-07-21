package com.votamas.api.common.web;

import com.votamas.model.exception.AuthenticationException;
import com.votamas.model.exception.MessageError;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class AuthenticatedUserIdResolver {

    public Mono<UUID> resolve(ServerRequest request) {
        return request.principal()
                .filter(JwtAuthenticationToken.class::isInstance)
                .cast(JwtAuthenticationToken.class)
                .map(authentication -> authentication.getToken().getClaimAsString("userId"))
                .flatMap(this::parse)
                .switchIfEmpty(Mono.error(
                        new AuthenticationException(MessageError.AUTHENTICATION_REQUIRED)));
    }

    private Mono<UUID> parse(String value) {
        if (value == null || value.isBlank()) {
            return Mono.error(new AuthenticationException(MessageError.AUTHENTICATION_REQUIRED));
        }
        try {
            return Mono.just(UUID.fromString(value));
        } catch (IllegalArgumentException exception) {
            return Mono.error(new AuthenticationException(MessageError.AUTHENTICATION_REQUIRED));
        }
    }
}
