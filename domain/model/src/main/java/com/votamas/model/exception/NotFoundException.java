package com.votamas.model.exception;

public class NotFoundException extends BusinessException {
    public NotFoundException(MessageError messageError) {
        super(messageError);
    }
}
