package com.votamas.api.observability;

import lombok.experimental.UtilityClass;
import org.springframework.web.server.ServerWebExchange;

@UtilityClass
public class RequestTracing {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String REQUEST_ID_ATTRIBUTE = RequestTracing.class.getName() + ".requestId";
    public static final String REQUEST_ID_CONTEXT = REQUEST_ID_ATTRIBUTE;

    public static String requestId(ServerWebExchange exchange) {
        Object value = exchange.getAttribute(REQUEST_ID_ATTRIBUTE);
        return value instanceof String requestId ? requestId : exchange.getRequest().getId();
    }
}
