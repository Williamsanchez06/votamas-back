package com.votamas.config;

import com.votamas.model.auth.gateways.TokenGateway;
import com.votamas.model.user.gateways.UserRepository;
import com.votamas.model.auth.gateways.PasswordGateway;
import com.votamas.usecase.getallusers.GetAllUsersUseCase;
import com.votamas.usecase.login.LoginUseCase;
import com.votamas.usecase.login.CreateUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    // Bean para crear usuarios
    @Bean
    public CreateUserUseCase createUserUseCase(UserRepository userRepository,
                                               PasswordGateway passwordGateway) {
        return new CreateUserUseCase(userRepository, passwordGateway);
    }

    // Bean para login
    @Bean
    public LoginUseCase loginUseCase(UserRepository userRepository,
                                     PasswordGateway passwordGateway,
                                     TokenGateway tokenGateway) {
        return new LoginUseCase(userRepository, passwordGateway, tokenGateway);
    }

    // Bean para obtener todos los usuarios
    @Bean
    public GetAllUsersUseCase getAllUsersUseCase(UserRepository userRepository) {
        return new GetAllUsersUseCase(userRepository);
    }
}

