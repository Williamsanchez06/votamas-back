package com.votamas.api;

import com.votamas.model.auth.Login;
import com.votamas.usecase.login.LoginUseCase;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class AuthHandler {

    private final LoginUseCase loginUseCase;

    public AuthHandler(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(Login.class)
                .map(loginUseCase::execute)
                .flatMap(token -> ServerResponse.ok().bodyValue(token));
    }
}
