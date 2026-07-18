package com.votamas.api.exceptions;

import com.votamas.model.exception.MessageError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class SecurityExceptionHandler {
    public Mono<Void> handle(ServerWebExchange exchange, MessageError messageError, Throwable exception) {
        int status = Integer.parseInt(HttpStatusExceptionMap.get(messageError.getCode()));
        ErrorResponse error = new ErrorResponse(messageError.getCode(), messageError.getMessage());
        log.warn("Security request rejected: requestId={}, method={}, path={}, status={}, code={}, reason={}",
                exchange.getRequest().getId(), exchange.getRequest().getMethod(),
                exchange.getRequest().getPath().value(), status, error.code(), exception.getMessage());

        String json = "{\"code\":\"" + escape(error.code()) + "\",\"message\":\""
                + escape(error.message()) + "\"}";
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponse().setRawStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n");
    }
}
