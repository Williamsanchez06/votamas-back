package com.votamas.usecase.user;

import com.votamas.model.auth.gateways.PasswordHasher;
import com.votamas.model.user.User;
import com.votamas.model.user.gateways.UserRepository;
import com.votamas.model.exception.ConflictException;
import com.votamas.model.exception.MessageError;
import com.votamas.model.exception.NotFoundException;
import com.votamas.model.common.pagination.PageRequest;
import com.votamas.model.common.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public Mono<User> saveUser(User user) {
        return userRepository.existsByEmail(user.email())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ConflictException(MessageError.EMAIL_ALREADY_REGISTERED));
                    }

                    String hashedPassword = passwordHasher.hash(user.password());

                    User userWithHashedPassword = user.toBuilder()
                            .password(hashedPassword)
                            .build();

                    return userRepository.save(userWithHashedPassword);
                });
    }

    public Mono<PageResult<User>> getAllUsers(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest);
    }

    public Mono<User> updateUser(UUID id, User user) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(MessageError.NO_USER_FOUND)))
                .flatMap(existing -> {
                    User updated = existing.toBuilder()
                            .name(user.name())
                            .surname(user.surname())
                            .email(user.email())
                            .build();

                    return userRepository.save(updated);
                });
    }

    public Mono<User> changeUserStatus(UUID id, boolean active) {
        return userRepository.updateStatus(id, active)
                .switchIfEmpty(Mono.error(new NotFoundException(MessageError.NO_USER_FOUND)));
    }
}



