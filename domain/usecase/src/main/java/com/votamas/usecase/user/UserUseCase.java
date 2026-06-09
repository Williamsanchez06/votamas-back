package com.votamas.usecase.user;

import com.votamas.model.auth.gateways.PasswordHasher;
import com.votamas.model.user.User;
import com.votamas.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public Mono<User> saveUser(User user) {
        return userRepository.existsByEmail(user.email())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("El email ya está registrado"));
                    }

                    String hashedPassword = passwordHasher.hash(user.password());

                    User userWithHashedPassword = user.toBuilder()
                            .password(hashedPassword)
                            .build();

                    return userRepository.save(userWithHashedPassword);
                });
    }

    public Flux<User> getAllUser() {
        return userRepository.findAll();
    }

}
