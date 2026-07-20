package com.votamas.api.exceptions;

import com.votamas.api.observability.RequestTracing;
import com.votamas.api.validation.InvalidRequestException;
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
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@Order(-2)
@Slf4j
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, WebProperties resources,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, resources.getResources(), applicationContext);
        setMessageReaders(serverCodecConfigurer.getReaders());
        setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        return Mono.defer(() -> Mono.error(getError(request))
                        .onErrorResume(InvalidRequestException.class,
                                error -> handleInvalidRequest(error, request))
                        .onErrorResume(BusinessException.class, error -> handleBusinessException(error, request))
                        .onErrorResume(ServerWebInputException.class,
                                error -> handleExpectedError(ApiError.INVALID_REQUEST, error, request))
                        .onErrorResume(NoResourceFoundException.class,
                                error -> handleExpectedError(ApiError.RESOURCE_NOT_FOUND, error, request))
                        .onErrorResume(error -> handleUnexpectedException(error, request)))
                .cast(ServerResponse.class);
    }

    private Mono<ServerResponse> handleInvalidRequest(InvalidRequestException error, ServerRequest request) {
        var fields = error.errors().stream().map(fieldError -> fieldError.field()).distinct().toList();
        log.warn("event=INVALID_REQUEST requestId={} method={} path={} status=400 code={} invalidFields={}",
                requestId(request), request.method(), request.path(), ApiError.INVALID_REQUEST.code(), fields);
        var response = new ErrorResponse(ApiError.INVALID_REQUEST.code(), error.getMessage(),
                ApiError.INVALID_REQUEST.status(), requestId(request), Instant.now(), error.errors());
        return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue(response);
    }

    private Mono<ServerResponse> handleBusinessException(BusinessException error, ServerRequest request) {
        int status = HttpStatusExceptionMap.get(error.getMessageError().getCode());
        log.warn("event=CONTROLLED_ERROR requestId={} method={} path={} status={} code={} exception={} message={}",
                requestId(request), request.method(), request.path(), status,
                error.getMessageError().getCode(), error.getClass().getSimpleName(), safeMessage(error));
        return buildResponse(error.getMessageError().getCode(), error.getMessageError().getMessage(), status, request);
    }

    private Mono<ServerResponse> handleExpectedError(ApiError apiError, Throwable error, ServerRequest request) {
        log.warn("event=REQUEST_REJECTED requestId={} method={} path={} status={} code={} exception={} message={}",
                requestId(request), request.method(), request.path(), apiError.status(), apiError.code(),
                error.getClass().getSimpleName(), apiError.message());
        return buildResponse(apiError.code(), apiError.message(), apiError.status(), request);
    }

    private Mono<ServerResponse> handleUnexpectedException(Throwable error, ServerRequest request) {
        Throwable rootCause = rootCause(error);
        log.error("event=UNEXPECTED_ERROR requestId={} method={} path={} exception={} rootCause={} rootMessage={}",
                requestId(request), request.method(), request.path(), error.getClass().getName(),
                rootCause.getClass().getName(), safeMessage(rootCause), error);
        return buildResponse(ApiError.INTERNAL_ERROR.code(), ApiError.INTERNAL_ERROR.message(),
                ApiError.INTERNAL_ERROR.status(), request);
    }

    private Mono<ServerResponse> buildResponse(String code, String message, int status, ServerRequest request) {
        var errorResponse = new ErrorResponse(code, message, status, requestId(request), Instant.now());
        return ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON).bodyValue(errorResponse);
    }

    private String requestId(ServerRequest request) {
        return RequestTracing.requestId(request.exchange());
    }

    private Throwable rootCause(Throwable error) {
        Throwable current = error;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    private String safeMessage(Throwable error) {
        String message = error.getMessage();
        return message == null ? "-" : message.replaceAll("[\\r\\n]", " ");
    }
}
