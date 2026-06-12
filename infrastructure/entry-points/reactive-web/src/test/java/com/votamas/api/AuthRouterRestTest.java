package com.votamas.api;

import com.votamas.api.auth.handlers.AuthHandler;
import com.votamas.api.auth.routers.AuthRouterRest;
import com.votamas.api.utils.ApiProperties;
import com.votamas.usecase.login.LoginUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {AuthRouterRest.class, AuthHandler.class, ApiProperties.class})
@WebFluxTest
class AuthRouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @Test
    void testListenPOSTLogin() {
        when(loginUseCase.execute(any())).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/v1/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"email\":\"test@test.com\",\"password\":\"1234\"}")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
}