package com.votamas.usecase.auth;

import com.votamas.model.auth.Login;
import com.votamas.model.auth.UserPermission;
import com.votamas.model.auth.gateways.PasswordHasher;
import com.votamas.model.auth.gateways.TokenProvider;
import com.votamas.model.auth.gateways.UserPermissionRepository;
import com.votamas.model.exception.AuthenticationException;
import com.votamas.model.user.User;
import com.votamas.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class LoginUseCaseTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserPermissionRepository userPermissionRepository =
            mock(UserPermissionRepository.class);
    private final TokenProvider tokenProvider = mock(TokenProvider.class);
    private final PasswordHasher passwordHasher = mock(PasswordHasher.class);
    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(
                userRepository, userPermissionRepository, tokenProvider, passwordHasher);
    }

    @Test
    void shouldLoginActiveUserWithValidPassword() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email("leader@example.com")
                .password("encoded-password")
                .active(true)
                .build();
        UserPermission permission = new UserPermission("LIDER", "GET_POTENTIAL_VOTER");
        Login login = new Login("leader@example.com", "raw-password");
        when(userRepository.findByEmail(login.email())).thenReturn(Mono.just(user));
        when(passwordHasher.matches(login.password(), user.password())).thenReturn(Mono.just(true));
        when(userPermissionRepository.findPermissionsByUserId(userId))
                .thenReturn(Flux.just(permission));
        when(tokenProvider.generateAccessToken(user, List.of(permission)))
                .thenReturn("signed-token");

        StepVerifier.create(loginUseCase.execute(login))
                .expectNextMatches(token -> "signed-token".equals(token.accessToken()))
                .verifyComplete();
    }

    @Test
    void shouldRejectInvalidCredentialsBeforeLoadingPermissions() {
        Login login = new Login("missing@example.com", "password");
        when(userRepository.findByEmail(login.email())).thenReturn(Mono.empty());

        StepVerifier.create(loginUseCase.execute(login))
                .expectError(AuthenticationException.class)
                .verify();

        verifyNoInteractions(passwordHasher, userPermissionRepository, tokenProvider);
    }
}
