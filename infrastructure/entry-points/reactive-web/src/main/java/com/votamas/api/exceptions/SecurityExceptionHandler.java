package com.votamas.api.exceptions;

import com.votamas.api.common.observability.RequestTracing;
import com.votamas.model.exception.MessageError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Slf4j
@Component
public class SecurityExceptionHandler {
    private final ObjectMapper objectMapper;

    public SecurityExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Mono<Void> handle(ServerWebExchange exchange, MessageError messageError, Throwable exception) {
        int status = HttpStatusExceptionMap.get(messageError.getCode());
        String requestId = RequestTracing.requestId(exchange);
        ErrorResponse error = new ErrorResponse(messageError.getCode(), messageError.getMessage(), status,
                requestId, Instant.now());
        log.warn("event=SECURITY_REJECTION requestId={} method={} path={} status={} code={} exception={} message={}",
                requestId, exchange.getRequest().getMethod(), exchange.getRequest().getPath().value(),
                status, error.code(), exception.getClass().getSimpleName(), sanitize(error.message()));

        return writeResponse(exchange, error);
    }

    public Mono<Void> handleRateLimit(ServerWebExchange exchange, long retryAfterSeconds, String policy) {
        ApiError apiError = ApiError.RATE_LIMIT_EXCEEDED;
        ErrorResponse error = new ErrorResponse(apiError.code(), apiError.message(), apiError.status(),
                RequestTracing.requestId(exchange), Instant.now());
        log.warn("event=RATE_LIMIT_EXCEEDED requestId={} method={} path={} status={} code={} policy={} retryAfter={}",
                error.requestId(), exchange.getRequest().getMethod(), exchange.getRequest().getPath().value(),
                error.status(), error.code(), policy, retryAfterSeconds);
        exchange.getResponse().getHeaders().set(HttpHeaders.RETRY_AFTER, Long.toString(retryAfterSeconds));
        return writeResponse(exchange, error);
    }

    private Mono<Void> writeResponse(ServerWebExchange exchange, ErrorResponse error) {
        exchange.getResponse().setRawStatusCode(error.status());
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer body = exchange.getResponse().bufferFactory().wrap(objectMapper.writeValueAsBytes(error));
        return exchange.getResponse().writeWith(Mono.just(body));
    }

    private String sanitize(String value) {
        return value == null ? "-" : value.replaceAll("[\\r\\n]", " ");
    }
}
