package com.votamas.api.common.observability;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpRequestLoggingFilter implements WebFilter {

    private static final Pattern SAFE_REQUEST_ID = Pattern.compile("[A-Za-z0-9._:-]{1,100}");
    private static final String UNHANDLED_ERROR_ATTRIBUTE = HttpRequestLoggingFilter.class.getName() + ".error";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestId = resolveRequestId(exchange);
        long startedAt = System.nanoTime();

        exchange.getAttributes().put(RequestTracing.REQUEST_ID_ATTRIBUTE, requestId);
        exchange.getResponse().getHeaders().set(RequestTracing.REQUEST_ID_HEADER, requestId);

        var request = exchange.getRequest();
        String page = safeQueryValue(exchange, "page");
        String size = safeQueryValue(exchange, "size");
        log.info("event=REQUEST_START requestId={} method={} path={} page={} size={}",
                requestId, request.getMethod(), request.getPath().value(), page, size);

        return chain.filter(exchange)
                .doOnError(error -> exchange.getAttributes().put(UNHANDLED_ERROR_ATTRIBUTE, Boolean.TRUE))
                .doFinally(signal -> logCompletion(exchange, requestId, startedAt))
                .contextWrite(context -> context.put(RequestTracing.REQUEST_ID_CONTEXT, requestId));
    }

    private void logCompletion(ServerWebExchange exchange, String requestId, long startedAt) {
        long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
        HttpStatusCode status = exchange.getResponse().getStatusCode();
        int statusCode = status == null
                ? (exchange.getAttributeOrDefault(UNHANDLED_ERROR_ATTRIBUTE, Boolean.FALSE) ? 500 : 200)
                : status.value();
        Object pattern = exchange.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String operation = pattern == null ? exchange.getRequest().getPath().value() : pattern.toString();
        String outcome = statusCode >= 400 ? "ERROR" : "SUCCESS";

        log.info("event=REQUEST_END requestId={} method={} path={} operation={} status={} outcome={} durationMs={}",
                requestId, exchange.getRequest().getMethod(), exchange.getRequest().getPath().value(),
                operation, statusCode, outcome, durationMs);
    }

    private String resolveRequestId(ServerWebExchange exchange) {
        String provided = exchange.getRequest().getHeaders().getFirst(RequestTracing.REQUEST_ID_HEADER);
        return provided != null && SAFE_REQUEST_ID.matcher(provided).matches()
                ? provided
                : UUID.randomUUID().toString();
    }

    private String safeQueryValue(ServerWebExchange exchange, String name) {
        String value = exchange.getRequest().getQueryParams().getFirst(name);
        return value == null ? "-" : value.replaceAll("[^0-9-]", "?");
    }
}
