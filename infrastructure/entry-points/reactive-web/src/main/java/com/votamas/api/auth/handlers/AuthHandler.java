package com.votamas.api.auth.handlers;

import com.votamas.api.auth.dtos.LoginRequest;
import com.votamas.api.auth.mappers.LoginMapper;
import com.votamas.usecase.login.LoginUseCase;
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

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .map(LoginMapper.INSTANCE::toLogin)
                .flatMap(loginUseCase::execute)
                .map(LoginMapper.INSTANCE::toLoginDTO)
                .flatMap(loginResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(loginResponse)
                );
    }

}
