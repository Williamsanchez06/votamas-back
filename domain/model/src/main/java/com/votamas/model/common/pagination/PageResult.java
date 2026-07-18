package com.votamas.model.common.pagination;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(List<T> content, int page, int size, long totalElements,
                            int totalPages, boolean first, boolean last) {
    public static <T> PageResult<T> of(List<T> content, PageRequest request, long totalElements) {
        int pages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / request.size());
        return new PageResult<>(List.copyOf(content), request.page(), request.size(), totalElements, pages,
                request.page() == 0, pages == 0 || request.page() >= pages - 1);
    }

    public <R> PageResult<R> map(Function<T, R> mapper) {
        return new PageResult<>(content.stream().map(mapper).toList(), page, size, totalElements,
                totalPages, first, last);
    }
}
