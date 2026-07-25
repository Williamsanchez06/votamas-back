package com.votamas.api.common.observability;

import java.util.regex.Pattern;

public final class LogMessageSanitizer {
    private static final Pattern LINE_BREAKS = Pattern.compile("[\\r\\n]");

    private LogMessageSanitizer() {
    }

    public static String sanitize(String message) {
        return message == null ? "-" : LINE_BREAKS.matcher(message).replaceAll(" ");
    }
}
