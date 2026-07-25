package com.votamas.api;

import com.votamas.api.auth.handlers.AuthHandler;
import com.votamas.api.auth.dtos.CurrentUserResponseDTO;
import com.votamas.api.auth.mappers.CurrentUserMapper;
import com.votamas.api.auth.mappers.LoginMapperImpl;
import com.votamas.api.auth.routers.AuthRouterRest;
import com.votamas.api.common.web.AuthenticatedUserIdResolver;
import com.votamas.api.config.ApiProperties;
import com.votamas.api.exceptions.GlobalExceptionHandler;
import com.votamas.api.common.observability.HttpRequestLoggingFilter;
import com.votamas.api.common.validation.RequestValidator;
import com.votamas.model.auth.CurrentUserProfile;
import com.votamas.model.auth.Token;
import com.votamas.usecase.auth.CurrentUserUseCase;
import com.votamas.usecase.auth.LoginUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

@ContextConfiguration(classes = {AuthRouterRest.class, AuthHandler.class, LoginMapperImpl.class,
        ApiProperties.class})
@WebFluxTest
@Import({GlobalExceptionHandler.class, HttpRequestLoggingFilter.class, RequestValidator.class})
class AuthRouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @MockitoBean
    private CurrentUserUseCase currentUserUseCase;

    @MockitoBean
    private AuthenticatedUserIdResolver authenticatedUserIdResolver;

    @MockitoBean
    private CurrentUserMapper currentUserMapper;

    @Test
    void testListenPOSTLogin() {
        when(loginUseCase.execute(any())).thenReturn(Mono.just(new Token("safe-token")));

        webTestClient.post()
                .uri("/api/v1/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"email\":\"test@test.com\",\"password\":\"1234\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldGetCurrentUserFromAuthHandler() {
        UUID userId = UUID.randomUUID();
        CurrentUserProfile profile = new CurrentUserProfile(
                userId, "Ana", "Pérez", "ana@example.com", true,
                List.of("LIDER"), List.of());
        CurrentUserResponseDTO response = new CurrentUserResponseDTO(
                userId, "Ana", "Pérez", "ana@example.com", true,
                List.of("LIDER"), List.of());
        when(authenticatedUserIdResolver.resolve(any())).thenReturn(Mono.just(userId));
        when(currentUserUseCase.execute(userId)).thenReturn(Mono.just(profile));
        when(currentUserMapper.toResponse(profile)).thenReturn(response);

        webTestClient.get()
                .uri("/api/v1/auth/me")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(userId.toString())
                .jsonPath("$.roles[0]").isEqualTo("LIDER");
    }

    @Test
    void shouldRejectMissingFieldsBeforeUseCase() {
        assertInvalid("{}", "email", "password");
    }

    @Test
    void shouldRejectBlankEmailBeforeUseCase() {
        assertInvalid("{\"email\":\"   \",\"password\":\"1234\"}", "email");
    }

    @Test
    void shouldRejectInvalidEmailBeforeUseCase() {
        assertInvalid("{\"email\":\"invalid-email\",\"password\":\"1234\"}", "email");
    }

    @Test
    void shouldRejectBlankPasswordBeforeUseCase() {
        assertInvalid("{\"email\":\"test@test.com\",\"password\":\"   \"}", "password");
    }

    private void assertInvalid(String body, String... fields) {
        var bodySpec = webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BP400")
                .jsonPath("$.message").isEqualTo("La petición no contiene datos válidos")
                .jsonPath("$.requestId").exists();
        for (String field : fields) {
            bodySpec.jsonPath("$.errors[?(@.field == '%s')]".formatted(field)).exists();
        }
        verifyNoInteractions(loginUseCase);
    }
}
