package com.votamas.model.user.gateways;

import com.votamas.model.user.User;
import com.votamas.model.common.pagination.PageQuery;
import com.votamas.model.common.pagination.PageResult;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface UserRepository {

    Mono<User> findById(UUID id);

    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> isActiveById(UUID id);

    Mono<User> save(User user);

    Mono<User> saveAsLeader(User user);

    Mono<PageResult<User>> findAll(PageQuery pageQuery);

    Mono<User> updateStatus(UUID id, boolean active);

}
