package com.votamas.api.handlers;

import com.votamas.api.dtos.CreateUserRequest;
import com.votamas.api.dtos.CreateUserResponse;
import com.votamas.model.user.gateways.User;
import com.votamas.usecase.login.CreateUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
public class UserHandler {

    private final CreateUserUseCase createUserUseCase;

    public UserHandler(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping
    public Mono<ResponseEntity<CreateUserResponse>> createUser(@RequestBody CreateUserRequest request) {

        User user = new User(
                null,
                request.name(),
                request.surname(),
                request.email(),
                request.password()
        );

        return createUserUseCase.execute(user)
                .map(createdUser -> new CreateUserResponse(
                        createdUser.id(),  // Ya es UUID
                        createdUser.name(),
                        createdUser.surname(),
                        createdUser.email()
                ))
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
}