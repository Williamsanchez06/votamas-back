package com.votamas.model.auth.gateways;

import com.votamas.model.auth.UserPermission;
import com.votamas.model.user.User;

import java.util.List;

public interface TokenRepository {
    String generateAccessToken(User user, List<UserPermission> userPermissions);
}
