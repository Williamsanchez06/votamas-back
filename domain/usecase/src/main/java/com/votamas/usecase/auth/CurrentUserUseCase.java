package com.votamas.usecase.auth;

import com.votamas.model.auth.CurrentUserProfile;
import com.votamas.model.auth.gateways.UserAccessRepository;
import com.votamas.model.exception.MessageError;
import com.votamas.model.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class CurrentUserUseCase {
    private final UserAccessRepository userAccessRepository;

    public Mono<CurrentUserProfile> execute(UUID userId) {
        return userAccessRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new NotFoundException(MessageError.NO_USER_FOUND)));
    }
}
