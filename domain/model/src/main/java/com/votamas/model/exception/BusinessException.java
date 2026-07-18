package com.votamas.model.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final MessageError messageError;

    public BusinessException(MessageError messageError) {
        super(messageError.getMessage());
        this.messageError = messageError;
    }

    public BusinessException(MessageError messageError, Throwable cause) {
        super(messageError.getMessage(), cause);
        this.messageError = messageError;
    }

}
