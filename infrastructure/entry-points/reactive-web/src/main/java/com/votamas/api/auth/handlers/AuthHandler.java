package com.votamas.api.auth.handlers;

import com.votamas.api.auth.dtos.LoginRequest;
import com.votamas.api.auth.mappers.CurrentUserMapper;
import com.votamas.api.auth.mappers.LoginMapper;
import com.votamas.api.common.validation.RequestValidator;
import com.votamas.api.common.web.AuthenticatedUserIdResolver;
import com.votamas.usecase.auth.CurrentUserUseCase;
import com.votamas.usecase.auth.LoginUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final LoginUseCase loginUseCase;
    private final CurrentUserUseCase currentUserUseCase;
    private final RequestValidator requestValidator;
    private final AuthenticatedUserIdResolver authenticatedUserIdResolver;
    private final LoginMapper loginMapper;
    private final CurrentUserMapper currentUserMapper;

    public Mono<ServerResponse> login(ServerRequest request) {
        return requestValidator.body(request, LoginRequest.class)
                .map(loginMapper::toLogin)
                .flatMap(loginUseCase::execute)
                .map(loginMapper::toLoginDTO)
                .flatMap(loginResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(loginResponse)
                );
    }

    public Mono<ServerResponse> getCurrentUser(ServerRequest request) {
        return authenticatedUserIdResolver.resolve(request)
                .flatMap(currentUserUseCase::execute)
                .map(currentUserMapper::toResponse)
                .flatMap(profile -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(profile));
    }
}
