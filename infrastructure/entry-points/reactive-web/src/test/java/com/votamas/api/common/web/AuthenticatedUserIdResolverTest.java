package com.votamas.api.common.web;

import com.votamas.model.exception.AuthenticationException;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.security.Principal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;

class AuthenticatedUserIdResolverTest {
    private final AuthenticatedUserIdResolver resolver = new AuthenticatedUserIdResolver();

    @Test
    void shouldResolveUserIdFromJwt() {
        UUID userId = UUID.randomUUID();
        ServerRequest request = requestWithClaim(userId.toString());

        StepVerifier.create(resolver.resolve(request)).expectNext(userId).verifyComplete();
    }

    @Test
    void shouldRejectMissingPrincipalOrInvalidClaim() {
        ServerRequest missing = mock(ServerRequest.class);
        when(missing.principal()).thenReturn(Mono.empty());
        StepVerifier.create(resolver.resolve(missing)).expectError(AuthenticationException.class).verify();

        StepVerifier.create(resolver.resolve(requestWithClaim("invalid")))
                .expectError(AuthenticationException.class).verify();
    }

    private ServerRequest requestWithClaim(String userId) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("userId", userId)
                .build();
        ServerRequest request = mock(ServerRequest.class);
        Mono<Principal> principal = Mono.just(new JwtAuthenticationToken(jwt));
        doReturn(principal).when(request).principal();
        return request;
    }
}
