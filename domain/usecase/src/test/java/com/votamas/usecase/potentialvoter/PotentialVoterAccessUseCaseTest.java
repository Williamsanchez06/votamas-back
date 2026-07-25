package com.votamas.usecase.potentialvoter;

import com.votamas.model.auth.gateways.UserRoleRepository;
import com.votamas.model.common.pagination.PageQuery;
import com.votamas.model.exception.BusinessException;
import com.votamas.model.potentialvoter.PotentialVoterSearchCriteria;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PotentialVoterAccessUseCaseTest {
    private final UserRoleRepository userRoleRepository = mock(UserRoleRepository.class);
    private final PotentialVoterAccessUseCase useCase =
            new PotentialVoterAccessUseCase(userRoleRepository);

    @Test
    void shouldPreserveAdministratorFilters() {
        UUID administratorId = UUID.randomUUID();
        UUID requestedLeaderId = UUID.randomUUID();
        var criteria = criteria(requestedLeaderId);
        when(userRoleRepository.hasActiveRole(administratorId, "ADMINISTRADOR"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(useCase.scope(criteria, administratorId))
                .assertNext(result -> assertThat(result).isSameAs(criteria))
                .verifyComplete();
    }

    @Test
    void shouldForceAuthenticatedLeaderScope() {
        UUID leaderId = UUID.randomUUID();
        var criteria = criteria(UUID.randomUUID());
        when(userRoleRepository.hasActiveRole(leaderId, "ADMINISTRADOR"))
                .thenReturn(Mono.just(false));

        StepVerifier.create(useCase.scope(criteria, leaderId))
                .assertNext(result -> {
                    assertThat(result.assignedLeaderId()).isEqualTo(leaderId);
                    assertThat(result.identification()).isEqualTo(criteria.identification());
                    assertThat(result.pagination()).isEqualTo(criteria.pagination());
                })
                .verifyComplete();
    }

    @Test
    void shouldRejectAnotherLeadersRecord() {
        UUID leaderId = UUID.randomUUID();
        when(userRoleRepository.hasActiveRole(leaderId, "ADMINISTRADOR"))
                .thenReturn(Mono.just(false));

        StepVerifier.create(useCase.verifyOwnership(leaderId, UUID.randomUUID()))
                .expectError(BusinessException.class)
                .verify();
    }

    private PotentialVoterSearchCriteria criteria(UUID assignedLeaderId) {
        return new PotentialVoterSearchCriteria(
                new PageQuery(0, 10),
                "123",
                null,
                null,
                assignedLeaderId
        );
    }
}
