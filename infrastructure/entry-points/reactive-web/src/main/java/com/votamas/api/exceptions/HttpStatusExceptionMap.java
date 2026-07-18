package com.votamas.api.exceptions;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class HttpStatusExceptionMap {

    private static final Map<String, String> httpStatusException;

    private final String INTERNAL_SERVER_ERROR = "500";

    private final String RESOURCE_NOT_FOUND = "404";

    static {
        httpStatusException = new HashMap<>();
        httpStatusException.put("BP400", "400");
        httpStatusException.put("BP401", "401");
        httpStatusException.put("BP403", "403");
        httpStatusException.put("BP404", "404");
        httpStatusException.put("BP409", "409");
    }

    public static String get(String code) {
        return httpStatusException.containsKey(code) ? httpStatusException.get(code) : getDefaultStatus();
    }

    public static String getDefaultStatus() {
        return INTERNAL_SERVER_ERROR;
    }

}
