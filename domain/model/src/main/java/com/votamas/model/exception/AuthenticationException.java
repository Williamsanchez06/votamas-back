package com.votamas.model.exception;

public class AuthenticationException extends BusinessException {
    public AuthenticationException(MessageError messageError) {
        super(messageError);
    }
}
