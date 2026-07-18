package com.votamas.model.exception;

public class ConflictException extends BusinessException {
    public ConflictException(MessageError messageError) {
        super(messageError);
    }
}
