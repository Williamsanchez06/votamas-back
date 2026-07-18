package com.votamas.api.exceptions;

import com.votamas.model.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.webflux.autoconfigure.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.webflux.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@Slf4j
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(
            ErrorAttributes errorAttributes,
            WebProperties resources,
            ApplicationContext applicationContext,
            ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, resources.getResources(), applicationContext);
        this.setMessageReaders(serverCodecConfigurer.getReaders());
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        return Mono.defer(() -> Mono.error(getError(request))
                        .onErrorResume(BusinessException.class, error -> handleBusinessException(error, request))
                        .onErrorResume(error -> handleUnexpectedException(error, request)))
                .cast(ServerResponse.class);
    }

    private Mono<ServerResponse> handleBusinessException(BusinessException error, ServerRequest request) {
        log.warn("Controlled error: method={}, path={}, code={}, message={}", request.method(),
                request.path(), error.getMessageError().getCode(), error.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                error.getMessageError().getCode(),
                error.getMessageError().getMessage()
        );

        return buildResponse(errorResponse, request);
    }

    private Mono<ServerResponse> handleUnexpectedException(Throwable error, ServerRequest request) {
        log.error("Unexpected error: method={}, path={}", request.method(), request.path(), error);
        return ServerResponse.status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ErrorResponse("BP500", "Ocurrió un error interno"));
    }

    private Mono<ServerResponse> buildResponse(ErrorResponse error, final ServerRequest request) {
        final var errorResponse = ErrorResponse.builder()
                .code(error.code())
                .message(error.message())
                .build();

        final var status = HttpStatusExceptionMap.get(error.code());

        return ServerResponse.status(Integer.parseInt(status))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse)
                .doOnNext(response ->
                        request.attributes().put("RESPONSE_BODY", errorResponse)
                );

    }

}
