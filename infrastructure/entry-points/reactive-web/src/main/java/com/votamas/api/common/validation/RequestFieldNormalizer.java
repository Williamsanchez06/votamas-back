package com.votamas.api.common.validation;

import java.util.Locale;
import java.util.regex.Pattern;

public final class RequestFieldNormalizer {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private RequestFieldNormalizer() {
    }

    public static String normalizeText(String value) {
        return value == null ? null : WHITESPACE.matcher(value.trim()).replaceAll(" ");
    }

    public static String normalizeEmail(String value) {
        return value == null ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
