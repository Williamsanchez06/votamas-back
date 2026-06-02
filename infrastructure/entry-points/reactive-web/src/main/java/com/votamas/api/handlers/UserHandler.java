package com.votamas.api.handlers;

import com.votamas.api.dtos.CreateUserRequest;
import com.votamas.api.dtos.CreateUserResponse;
import com.votamas.model.user.gateways.User;
import com.votamas.usecase.login.CreateUserUseCase;
import com.votamas.usecase.getallusers.GetAllUsersUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
public class UserHandler {

    private final CreateUserUseCase createUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;

    public UserHandler(CreateUserUseCase createUserUseCase,
                       GetAllUsersUseCase getAllUsersUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
    }

    // Endpoint para crear usuario
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
                        createdUser.id(),
                        createdUser.name(),
                        createdUser.surname(),
                        createdUser.email()
                ))
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    // Endpoint para listar todos los usuarios
    @GetMapping
    public Flux<CreateUserResponse> getAllUsers() {
        return getAllUsersUseCase.execute()
                .map(user -> new CreateUserResponse(
                        user.id(),
                        user.name(),
                        user.surname(),
                        user.email()
                ));
    }
}