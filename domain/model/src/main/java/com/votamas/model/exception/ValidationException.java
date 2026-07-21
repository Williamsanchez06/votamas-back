package com.votamas.model.exception;

public class ValidationException extends BusinessException {
    public ValidationException(MessageError messageError) {
        super(messageError);
    }

    public ValidationException(MessageError messageError, Throwable cause) {
        super(messageError, cause);
    }
}
