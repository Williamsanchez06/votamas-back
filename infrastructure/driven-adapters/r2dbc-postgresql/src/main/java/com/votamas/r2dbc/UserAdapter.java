package com.votamas.r2dbc;

import com.votamas.model.user.User;
import com.votamas.model.user.gateways.UserRepository;
import com.votamas.r2dbc.mapper.UserRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserAdapter implements UserRepository {

    private final UserReactiveRepository repository;
    private final UserRepositoryMapper mapper;

    @Override
    public Mono<User> save(User user) {
        return repository.save(mapper.toUserData(user))
                .map(mapper::toUser);
    }
}
