package com.votamas.api.user.handlers;

import com.votamas.api.user.dtos.CreateUserRequestDTO;
import com.votamas.api.user.mappers.UserMapper;
import com.votamas.model.user.User;
import com.votamas.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final UserUseCase userUseCase;

    public Mono<ServerResponse> createUser(ServerRequest request) {

        return request.bodyToMono(CreateUserRequestDTO.class)
                .map(UserMapper.INSTANCE::toUser)
                .flatMap(userUseCase::saveUser)
                .flatMap(user -> ServerResponse.ok().bodyValue(user));

    }

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return ServerResponse.ok()
                .body(userUseCase.getAllUser(), User.class);
    }
}