package com.votamas.api.handlers;

import com.votamas.api.dtos.CreateUserRequest;
import com.votamas.api.mappers.UserMapper;
import com.votamas.model.user.User;
import com.votamas.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final UserUseCase userUseCase;

    public Mono<ServerResponse> createUser(ServerRequest request) {

        return request.bodyToMono(CreateUserRequest.class)
                .map(UserMapper.INSTANCE::toUser)
                .flatMap(userUseCase::saveUser)
                .flatMap(user -> ServerResponse.ok().bodyValue(user));

    }

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return ServerResponse.ok()
                .body(userUseCase.getAllUser(), User.class);
    }
}