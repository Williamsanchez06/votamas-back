package com.votamas.usecase.login;

import com.votamas.model.auth.Login;
import com.votamas.model.auth.Token;
import com.votamas.model.auth.gateways.PasswordGateway;
import com.votamas.model.auth.gateways.TokenGateway;
import com.votamas.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordGateway passwordGateway;
    private final TokenGateway tokenGateway;

    public Mono<Token> execute(Login login) {

        return userRepository.findByEmail(login.email()).switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado"))).flatMap(user -> {

            boolean passwordOk = passwordGateway.matches(login.password(), user.password());

            if (!passwordOk) {
                return Mono.error(new RuntimeException("Credenciales inválidas"));
            }

            String accessToken = tokenGateway.generateAccessToken(user);

            return Mono.just(new Token(accessToken));
        });
    }
}
