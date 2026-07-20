package com.votamas.api.exceptions;

import com.votamas.api.observability.HttpRequestLoggingFilter;
import com.votamas.model.exception.MessageError;
import com.votamas.model.exception.NotFoundException;
import com.votamas.model.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@WebFluxTest
@ContextConfiguration(classes = GlobalExceptionHandlerTest.Routes.class)
@Import({GlobalExceptionHandler.class, HttpRequestLoggingFilter.class, GlobalExceptionHandlerTest.Routes.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private WebTestClient client;

    @Test
    void shouldReturnControlledErrorWithRequestIdAndNoStackTrace() {
        client.get().uri("/controlled")
                .header("X-Request-Id", "test-404")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().valueEquals("X-Request-Id", "test-404")
                .expectBody()
                .jsonPath("$.code").isEqualTo("BP404")
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.requestId").isEqualTo("test-404")
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.trace").doesNotExist()
                .jsonPath("$.exception").doesNotExist();
    }

    @Test
    void shouldHideUnexpectedErrorDetails() {
        client.get().uri("/unexpected")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BP500")
                .jsonPath("$.message").isEqualTo("Ocurrió un error interno")
                .jsonPath("$.requestId").exists()
                .jsonPath("$.trace").doesNotExist()
                .jsonPath("$.exception").doesNotExist();
    }

    @Test
    void shouldReturnValidationError() {
        client.get().uri("/validation")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BP400")
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.requestId").exists();
    }

    @Configuration(proxyBeanMethods = false)
    static class Routes {
        @Bean
        RouterFunction<ServerResponse> errorRoutes() {
            return route()
                    .GET("/controlled", request -> Mono.error(
                            new NotFoundException(MessageError.NO_USER_FOUND)))
                    .GET("/unexpected", request -> Mono.error(
                            new IllegalStateException("internal database details")))
                    .GET("/validation", request -> Mono.error(
                            new ValidationException(MessageError.VALIDATION_ERROR)))
                    .build();
        }
    }
}
