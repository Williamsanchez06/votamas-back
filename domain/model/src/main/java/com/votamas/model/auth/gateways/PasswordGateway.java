package com.votamas.model.auth.gateways;

public interface PasswordGateway {

    boolean matches(String rawPassword, String encodedPassword);

}
