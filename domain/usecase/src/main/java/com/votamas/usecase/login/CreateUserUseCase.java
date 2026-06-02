package com.votamas.usecase.login;

import com.votamas.model.user.gateways.User;
import com.votamas.model.auth.gateways.PasswordGateway;
import com.votamas.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordGateway passwordGateway;

    public CreateUserUseCase(UserRepository userRepository, PasswordGateway passwordGateway) {
        this.userRepository = userRepository;
        this.passwordGateway = passwordGateway;
    }

    public Mono<User> execute(User user) {
        return userRepository.existsByEmail(user.email())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("El email ya está registrado"));
                    }

                    // Hashear contraseña
                    String hashedPassword = passwordGateway.hash(user.password());

                    // Crear usuario con ID generado
                    User userToSave = new User(
                            UUID.randomUUID(),  // Generamos el ID aquí
                            user.name(),
                            user.surname(),
                            user.email(),
                            hashedPassword
                    );

                    return userRepository.save(userToSave);
                });
    }
}