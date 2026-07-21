package com.votamas.api;

import com.votamas.api.auth.handlers.AuthHandler;
import com.votamas.api.auth.mappers.LoginMapperImpl;
import com.votamas.api.auth.routers.AuthRouterRest;
import com.votamas.api.config.ApiProperties;
import com.votamas.api.exceptions.GlobalExceptionHandler;
import com.votamas.api.common.observability.HttpRequestLoggingFilter;
import com.votamas.api.common.validation.RequestValidator;
import com.votamas.model.auth.Token;
import com.votamas.usecase.login.LoginUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

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
