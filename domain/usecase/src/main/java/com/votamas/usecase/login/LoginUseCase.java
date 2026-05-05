package com.votamas.usecase.login;

import com.votamas.model.auth.Login;
import com.votamas.model.auth.Token;
import com.votamas.model.auth.gateways.PasswordGateway;
import com.votamas.model.auth.gateways.TokenGateway;
import com.votamas.model.user.User;
import com.votamas.model.user.gateways.UserRepository;

public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordGateway passwordGateway;
    private final TokenGateway tokenGateway;

    public LoginUseCase(
            UserRepository userRepository,
            PasswordGateway passwordGateway,
            TokenGateway tokenGateway
    ) {
        this.userRepository = userRepository;
        this.passwordGateway = passwordGateway;
        this.tokenGateway = tokenGateway;
    }

    public Token execute(Login login) {
        User user = userRepository.findByEmail(login.getEmail());

        if (user == null) {
            throw new RuntimeException("Credenciales inválidas");
        }

        boolean passwordOk = passwordGateway.matches(
                login.getPassword(),
                user.getPassword()
        );

        if (!passwordOk) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return new Token(tokenGateway.generateAccessToken(user));
    }
}
