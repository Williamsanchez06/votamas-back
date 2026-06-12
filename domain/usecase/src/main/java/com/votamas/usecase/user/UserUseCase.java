package com.votamas.usecase.user;

import com.votamas.model.auth.gateways.PasswordRepository;
import com.votamas.model.user.User;
import com.votamas.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;


@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final PasswordRepository passwordRepository;

    public Mono<User> saveUser(User user) {
        return userRepository.existsByEmail(user.email())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("El email ya está registrado"));
                    }

                    String hashedPassword = passwordRepository.hash(user.password());

                    User userWithHashedPassword = user.toBuilder()
                            .password(hashedPassword)
                            .build();

                    return userRepository.save(userWithHashedPassword);
                });
    }

    public Flux<User> getAllUser() {
        return userRepository.findAll();
    }

    public Mono<User> updateUser(UUID id, User user) {
        return userRepository.update(id, user);
    }
}



