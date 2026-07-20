package com.votamas.api.user.handlers;

import com.votamas.api.user.dtos.UserRequestDTO;
import com.votamas.api.user.dtos.UserStatusRequestDTO;
import com.votamas.api.user.mappers.UserMapper;
import com.votamas.api.utils.PaginationRequestParser;
import com.votamas.model.exception.ValidationException;
import com.votamas.model.exception.MessageError;
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

    public Mono<ServerResponse> createUser(ServerRequest request) {

        return request.bodyToMono(UserRequestDTO.class)
                .map(UserMapper.INSTANCE::toUser)
                .flatMap(userUseCase::saveUser)
                .map(UserMapper.INSTANCE::toResponse)
                .flatMap(user -> ServerResponse.ok().bodyValue(user));

    }

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        var pageRequest = PaginationRequestParser.from(request);
        return userUseCase.getAllUsers(pageRequest)
                .map(result -> result.map(UserMapper.INSTANCE::toResponse))
                .flatMap(result -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(result));
    }

    public Mono<ServerResponse> updateUser(ServerRequest request) {
        UUID id = UUID.fromString(request.pathVariable("id"));

        return request.bodyToMono(UserRequestDTO.class)
                .map(UserMapper.INSTANCE::toUser)
                .flatMap(user -> userUseCase.updateUser(id, user))
                .map(UserMapper.INSTANCE::toResponse)
                .flatMap(user -> ServerResponse.ok().bodyValue(user));
    }

    public Mono<ServerResponse> changeUserStatus(ServerRequest request) {
        UUID id = UUID.fromString(request.pathVariable("id"));
        return request.bodyToMono(UserStatusRequestDTO.class)
                .filter(status -> status.active() != null)
                .flatMap(status -> userUseCase.changeUserStatus(id, status.active()))
                .map(UserMapper.INSTANCE::toResponse)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(Mono.error(new ValidationException(MessageError.VALIDATION_ERROR)));
    }

}
