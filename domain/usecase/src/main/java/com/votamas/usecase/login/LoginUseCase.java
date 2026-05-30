package com.votamas.usecase.login;

import com.votamas.model.auth.Login;
import com.votamas.model.auth.Token;
import com.votamas.model.auth.gateways.UserPermissionRepository;
import com.votamas.model.auth.gateways.TokenRepository;
import com.votamas.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final TokenRepository tokenGateway;

    public Mono<Token> execute(Login login) {

        return userRepository.findByEmail(login.email())
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                .flatMap(user ->
                        userPermissionRepository.findPermissionsByUserId(user.id())
                                .collectList()
                                .map(userPermissions -> {
                                    String accessToken = tokenGateway.generateAccessToken(user, userPermissions);
                                    return Token.builder()
                                            .accessToken(accessToken)
                                            .build();
                                })
                );
    }
}
