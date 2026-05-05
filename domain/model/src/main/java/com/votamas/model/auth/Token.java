package com.votamas.model.auth;

public class Token {
    private String accessToken;

    private final String tokenType = "Bearer";

    public Token(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}
