package com.votamas.api.user.handlers;

import com.votamas.api.user.dtos.UserCreateRequestDTO;
import com.votamas.api.user.dtos.UserStatusRequestDTO;
import com.votamas.api.user.dtos.UserUpdateRequestDTO;
import com.votamas.api.user.mappers.UserMapper;
import com.votamas.api.common.web.PaginationRequestParser;
import com.votamas.api.common.web.PathVariableParser;
import com.votamas.api.common.validation.RequestValidator;
import com.votamas.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final UserUseCase userUseCase;
    private final RequestValidator requestValidator;
    private final UserMapper userMapper;

    public Mono<ServerResponse> createUser(ServerRequest request) {

        return requestValidator.body(request, UserCreateRequestDTO.class)
                .map(userMapper::toUser)
                .flatMap(userUseCase::saveUser)
                .map(userMapper::toResponse)
                .flatMap(user -> ServerResponse.status(201).bodyValue(user));

    }

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        var pageRequest = PaginationRequestParser.from(request);
        return userUseCase.getAllUsers(pageRequest)
                .map(result -> result.map(userMapper::toResponse))
                .flatMap(result -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(result));
    }

    public Mono<ServerResponse> updateUser(ServerRequest request) {
        UUID id = PathVariableParser.uuid(request.pathVariable("id"), "id");

        return requestValidator.body(request, UserUpdateRequestDTO.class)
                .map(userMapper::toUser)
                .flatMap(user -> userUseCase.updateUser(id, user))
                .map(userMapper::toResponse)
                .flatMap(user -> ServerResponse.ok().bodyValue(user));
    }

    public Mono<ServerResponse> changeUserStatus(ServerRequest request) {
        UUID id = PathVariableParser.uuid(request.pathVariable("id"), "id");
        return requestValidator.body(request, UserStatusRequestDTO.class)
                .flatMap(status -> userUseCase.changeUserStatus(id, status.active()))
                .map(userMapper::toResponse)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(user));
    }

}
