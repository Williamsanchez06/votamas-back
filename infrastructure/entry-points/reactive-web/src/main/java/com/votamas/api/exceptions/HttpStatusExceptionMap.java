package com.votamas.api.exceptions;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class HttpStatusExceptionMap {

    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final Map<String, Integer> HTTP_STATUS_BY_CODE = Map.of(
            "BP400", 400,
            "BP401", 401,
            "BP403", 403,
            "BP404", 404,
            "BP409", 409
    );

    public static int get(String code) {
        return HTTP_STATUS_BY_CODE.getOrDefault(code, INTERNAL_SERVER_ERROR);
    }

    public static int getDefaultStatus() {
        return INTERNAL_SERVER_ERROR;
    }

}
