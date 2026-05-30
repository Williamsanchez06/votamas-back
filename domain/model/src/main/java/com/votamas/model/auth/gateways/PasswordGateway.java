package com.votamas.model.auth.gateways;

public interface PasswordGateway {
    String hash(String password);                     // ya existente
    boolean matches(String rawPassword, String hashedPassword);  // nuevo
}