package com.votamas.model.common.pagination;

public record PageQuery(int page, int size) {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    public PageQuery {
        if (page < 0) throw new IllegalArgumentException("El número de página no puede ser negativo");
        if (size < 1 || size > MAX_SIZE) {
            throw new IllegalArgumentException("El tamaño de página debe estar entre 1 y " + MAX_SIZE);
        }
    }

    public long offset() {
        return (long) page * size;
    }
}
