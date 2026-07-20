package com.votamas.api.common.observability;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestLoggingFilterTest {

    private final HttpRequestLoggingFilter filter = new HttpRequestLoggingFilter();

    @Test
    void shouldPropagateProvidedRequestIdInHeaderAttributeAndReactorContext() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest
                .get("/api/v1/users?page=0&size=10")
                .header(RequestTracing.REQUEST_ID_HEADER, "client-request-123"));

        Mono<Void> result = filter.filter(exchange, current -> Mono.deferContextual(context -> {
            String contextRequestId = context.get(RequestTracing.REQUEST_ID_CONTEXT);
            assertThat(contextRequestId).isEqualTo("client-request-123");
            current.getResponse().setStatusCode(HttpStatus.OK);
            return current.getResponse().setComplete();
        }));

        StepVerifier.create(result).verifyComplete();
        assertThat(RequestTracing.requestId(exchange)).isEqualTo("client-request-123");
        assertThat(exchange.getResponse().getHeaders().getFirst(RequestTracing.REQUEST_ID_HEADER))
                .isEqualTo("client-request-123");
    }

    @Test
    void shouldReplaceUnsafeRequestId() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/health")
                .header(RequestTracing.REQUEST_ID_HEADER, "unsafe request id\nvalue"));

        StepVerifier.create(filter.filter(exchange, current -> current.getResponse().setComplete()))
                .verifyComplete();

        assertThat(RequestTracing.requestId(exchange))
                .isNotEqualTo("unsafe request id\nvalue")
                .matches("[0-9a-f-]{36}");
    }
}
