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
import java.util.UUID;

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

    public Mono<ServerResponse> updateUser(ServerRequest request) {
        UUID id = UUID.fromString(request.pathVariable("id"));
        return request.bodyToMono(CreateUserRequestDTO.class)
                .map(UserMapper.INSTANCE::toUser)
                .flatMap(user -> userUseCase.updateUser(id, user))
                .flatMap(user -> ServerResponse.ok().bodyValue(user));
    }

    public Mono<ServerResponse> disableUser(ServerRequest request) {
        UUID id = UUID.fromString(request.pathVariable("id"));
        return userUseCase.disableUser(id)
                .flatMap(user -> ServerResponse.ok().bodyValue(user));
    }
}
