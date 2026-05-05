package com.votamas.model.user.gateways;

import com.votamas.model.user.User;

public interface UserRepository {

    User findByEmail(String email);

}
