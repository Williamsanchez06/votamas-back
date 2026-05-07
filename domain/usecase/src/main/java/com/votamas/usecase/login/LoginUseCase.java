package com.votamas.usecase.login;

import com.votamas.model.auth.Login;
import com.votamas.model.auth.Token;
import com.votamas.model.auth.gateways.PasswordGateway;
import com.votamas.model.auth.gateways.TokenGateway;
import com.votamas.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordGateway passwordGateway;
    private final TokenGateway tokenGateway;

    public LoginUseCase(UserRepository userRepository, PasswordGateway passwordGateway, TokenGateway tokenGateway) {
        this.userRepository = userRepository;
        this.passwordGateway = passwordGateway;
        this.tokenGateway = tokenGateway;
    }

    public Mono<Token> execute(Login login) {

        return userRepository.findByEmail(login.getEmail()).switchIfEmpty(Mono.error(new RuntimeException("Credenciales inválidas"))).flatMap(user -> {

            boolean passwordOk = passwordGateway.matches(login.getPassword(), user.getPassword());

            if (!passwordOk) {
                return Mono.error(new RuntimeException("Credenciales inválidas"));
            }

            String accessToken = tokenGateway.generateAccessToken(user);

            return Mono.just(new Token(accessToken));
        });
    }
}
