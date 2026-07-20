package com.votamas.api.utils;

import com.votamas.model.common.pagination.PageRequest;
import com.votamas.model.exception.MessageError;
import com.votamas.model.exception.ValidationException;
import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.server.ServerRequest;

@UtilityClass
public class PaginationRequestParser {

    public static PageRequest from(ServerRequest request) {
        try {
            int page = request.queryParam("page").map(Integer::parseInt).orElse(PageRequest.DEFAULT_PAGE);
            int size = request.queryParam("size").map(Integer::parseInt).orElse(PageRequest.DEFAULT_SIZE);
            return new PageRequest(page, size);
        } catch (IllegalArgumentException exception) {
            throw new ValidationException(MessageError.VALIDATION_ERROR);
        }
    }
}
