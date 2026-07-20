package com.votamas.model.common.pagination;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(List<T> content, int page, int size, long totalElements,
                            int totalPages) {
    public static <T> PageResult<T> of(List<T> content, PageRequest request, long totalElements) {
        if (totalElements < 0) {
            throw new IllegalArgumentException("El total de elementos no puede ser negativo");
        }
        long pages = totalElements / request.size();
        if (totalElements % request.size() != 0) {
            pages++;
        }
        return new PageResult<>(List.copyOf(content), request.page(), request.size(), totalElements,
                Math.toIntExact(pages));
    }

    public <R> PageResult<R> map(Function<T, R> mapper) {
        return new PageResult<>(content.stream().map(mapper).toList(), page, size, totalElements,
                totalPages);
    }
}
