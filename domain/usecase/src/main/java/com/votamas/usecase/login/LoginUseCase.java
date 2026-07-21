package com.votamas.usecase.login;

import com.votamas.model.auth.Login;
import com.votamas.model.auth.Token;
import com.votamas.model.auth.gateways.PasswordHasher;
import com.votamas.model.auth.gateways.UserPermissionRepository;
import com.votamas.model.auth.gateways.TokenProvider;
import com.votamas.model.user.gateways.UserRepository;
import com.votamas.model.exception.AuthenticationException;
import com.votamas.model.exception.MessageError;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final TokenProvider tokenGateway;
    private final PasswordHasher passwordHasher;

    public Mono<Token> execute(Login login) {

        return userRepository.findByEmail(login.email())
                .switchIfEmpty(Mono.error(new AuthenticationException(MessageError.INVALID_CREDENTIALS)))
                .filter(user -> Boolean.TRUE.equals(user.active()))
                .switchIfEmpty(Mono.error(new AuthenticationException(MessageError.INVALID_CREDENTIALS)))
                .flatMap(user -> {
                    return passwordHasher.matches(login.password(), user.password())
                            .filter(Boolean.TRUE::equals)
                            .switchIfEmpty(Mono.error(
                                    new AuthenticationException(MessageError.INVALID_CREDENTIALS)))
                            .then(userPermissionRepository.findPermissionsByUserId(user.id()).collectList())
                            .map(userPermissions -> Token.builder()
                                    .accessToken(tokenGateway.generateAccessToken(user, userPermissions))
                                    .build());
                });
    }
}
