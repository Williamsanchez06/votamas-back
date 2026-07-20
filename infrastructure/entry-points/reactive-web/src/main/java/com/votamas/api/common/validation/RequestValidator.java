package com.votamas.api.common.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RequestValidator {
    private static final FieldValidationError MISSING_BODY =
            new FieldValidationError("body", "El cuerpo de la petición es obligatorio");

    private final Validator validator;

    public <T> Mono<T> body(ServerRequest request, Class<T> bodyType, Class<?>... groups) {
        return request.bodyToMono(bodyType)
                .switchIfEmpty(Mono.error(new InvalidRequestException(List.of(MISSING_BODY))))
                .flatMap(value -> validate(value, groups));
    }

    public <T> Mono<T> validate(T value, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = groups.length == 0
                ? validator.validate(value)
                : validator.validate(value, groups);
        if (violations.isEmpty()) {
            return Mono.just(value);
        }

        List<FieldValidationError> errors = violations.stream()
                .map(violation -> new FieldValidationError(
                        violation.getPropertyPath().toString(), violation.getMessage()))
                .distinct()
                .sorted(Comparator.comparing(FieldValidationError::field)
                        .thenComparing(FieldValidationError::message))
                .toList();
        return Mono.error(new InvalidRequestException(errors));
    }
}
