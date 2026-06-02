package com.votamas.usecase.getallusers;


import com.votamas.model.user.gateways.User;
import com.votamas.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetAllUsersUseCase {

    private final UserRepository userRepository;

    public Flux<User> execute() {
        return userRepository.findAll();
    }
}