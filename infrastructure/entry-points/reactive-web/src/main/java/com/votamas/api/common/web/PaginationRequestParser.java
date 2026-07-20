package com.votamas.api.common.web;

import com.votamas.model.common.pagination.PageRequest;
import com.votamas.api.common.validation.FieldValidationError;
import com.votamas.api.common.validation.InvalidRequestException;
import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

@UtilityClass
public class PaginationRequestParser {

    public static PageRequest from(ServerRequest request) {
        int page = parseInteger(request, "page", PageRequest.DEFAULT_PAGE);
        int size = parseInteger(request, "size", PageRequest.DEFAULT_SIZE);
        if (page < 0) {
            throw invalid("page", "La página debe ser mayor o igual a 0");
        }
        if (size < 1 || size > PageRequest.MAX_SIZE) {
            throw invalid("size", "El tamaño debe estar entre 1 y " + PageRequest.MAX_SIZE);
        }
        return new PageRequest(page, size);
    }

    private int parseInteger(ServerRequest request, String field, int defaultValue) {
        String value = request.queryParam(field).orElse(null);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw invalid(field, "El valor debe ser un número entero");
        }
    }

    private InvalidRequestException invalid(String field, String message) {
        return new InvalidRequestException(List.of(new FieldValidationError(field, message)));
    }
}
