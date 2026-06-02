package com.votamas.usecase.login;

import com.votamas.model.auth.Login;
import com.votamas.model.auth.Token;
import com.votamas.model.auth.gateways.PasswordGateway;
import com.votamas.model.auth.gateways.TokenGateway;
import com.votamas.model.user.gateways.UserRepository;
import com.votamas.model.user.gateways.User;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordGateway passwordGateway;
    private final TokenGateway tokenGateway;

    public Mono<Token> execute(Login login) {

        System.out.println("Intentando login para email: " + login.email());

        return userRepository.findByEmail(login.email())
                .doOnNext(user -> System.out.println("Usuario encontrado en DB: " + user.email()))
                .switchIfEmpty(Mono.defer(() -> {
                    System.out.println("Usuario no encontrado en la base de datos");
                    return Mono.error(new RuntimeException("Usuario no encontrado"));
                }))
                .flatMap(user -> {
                    boolean passwordOk = passwordGateway.matches(login.password(), user.password());
                    System.out.println("Password correcto? " + passwordOk);

                    if (!passwordOk) {
                        System.out.println("Credenciales inválidas para: " + login.email());
                        return Mono.error(new RuntimeException("Credenciales inválidas"));
                    }

                    String accessToken = tokenGateway.generateAccessToken(user);
                    System.out.println("Login exitoso, generando token para: " + login.email());
                    return Mono.just(new Token(accessToken));
                });
    }
}