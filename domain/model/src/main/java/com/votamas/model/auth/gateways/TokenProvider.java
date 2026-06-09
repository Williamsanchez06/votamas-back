package com.votamas.model.auth.gateways;

import com.votamas.model.auth.UserPermission;
import com.votamas.model.user.User;

import java.util.List;

public interface TokenProvider {
    String generateAccessToken(User user, List<UserPermission> permissions);
}
