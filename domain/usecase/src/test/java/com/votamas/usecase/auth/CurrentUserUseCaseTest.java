package com.votamas.usecase.auth;

import com.votamas.model.auth.CurrentUserProfile;
import com.votamas.model.auth.gateways.UserAccessRepository;
import com.votamas.model.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrentUserUseCaseTest {
    private final UserAccessRepository userAccessRepository = mock(UserAccessRepository.class);
    private final CurrentUserUseCase currentUserUseCase =
            new CurrentUserUseCase(userAccessRepository);

    @Test
    void shouldReturnCurrentUserProfile() {
        UUID userId = UUID.randomUUID();
        CurrentUserProfile profile = new CurrentUserProfile(
                userId, "Ana", "Pérez", "ana@example.com", true,
                List.of("LIDER"), List.of());
        when(userAccessRepository.findByUserId(userId)).thenReturn(Mono.just(profile));

        StepVerifier.create(currentUserUseCase.execute(userId))
                .expectNext(profile)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenCurrentUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userAccessRepository.findByUserId(userId)).thenReturn(Mono.empty());

        StepVerifier.create(currentUserUseCase.execute(userId))
                .expectError(NotFoundException.class)
                .verify();
    }
}
