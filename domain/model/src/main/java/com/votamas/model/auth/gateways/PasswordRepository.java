package com.votamas.model.auth.gateways;

public interface PasswordRepository {

    boolean matches(String rawPassword, String encodedPassword);

}
