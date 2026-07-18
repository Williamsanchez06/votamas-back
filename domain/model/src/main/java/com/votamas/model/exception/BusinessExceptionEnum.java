package com.votamas.model.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessExceptionEnum {

    GENERIC_VALIDATION_ERROR(Constants.ERROR_400, Constants.CODE_400,
            "The request constains invalid data or missing requerid fields",
            Constants.TITTLE_400, "ER40303");

    private final String status;
    private final String code;
    private final String detail;
    private final String title;
    private final String userMessage;

    private static class Constants {
        private static final String ERROR_400 = "400";
        private static final String CODE_400 = "LFD2400";
        private static final String TITTLE_400 = "Bad request";
    }

}
