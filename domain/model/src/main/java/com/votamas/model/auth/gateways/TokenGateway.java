package com.votamas.model.auth.gateways;

import com.votamas.model.user.gateways.User;

public interface TokenGateway {
    String generateAccessToken(User user);
}
