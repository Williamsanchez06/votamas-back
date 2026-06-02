package com.votamas.model.auth.gateways;

public interface PasswordRepository {

    String hash(String password);

    boolean matches(String rawPassword, String encodedPassword);
}
