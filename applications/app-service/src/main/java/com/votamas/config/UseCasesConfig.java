package com.votamas.config;

import com.votamas.model.auth.gateways.PasswordRepository;
import com.votamas.model.auth.gateways.TokenRepository;
import com.votamas.model.auth.gateways.UserPermissionRepository;
import com.votamas.model.user.gateways.UserRepository;
import com.votamas.usecase.login.LoginUseCase;
import com.votamas.usecase.user.UserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

        // Bean para login
        @Bean
        public LoginUseCase loginUseCase(UserRepository userRepository,
                                         UserPermissionRepository userPermissionRepository,
                                         TokenRepository tokenRepository,
                                         PasswordRepository passwordRepository) {
                return new LoginUseCase(userRepository, userPermissionRepository, tokenRepository, passwordRepository);
        }

        // Bean para operaciones de usuario
        @Bean
        public UserUseCase userUseCase(UserRepository userRepository,
                                       PasswordRepository passwordRepository) {
                return new UserUseCase(userRepository, passwordRepository);
        }
}