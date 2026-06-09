package com.votamas.model.auth.gateways;

public interface PasswordHasher {

    String hash(String password);

    boolean matches(String rawPassword, String encodedPassword);
}
