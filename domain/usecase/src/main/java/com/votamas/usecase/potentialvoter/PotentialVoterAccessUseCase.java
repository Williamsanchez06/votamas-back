package com.votamas.usecase.potentialvoter;

import com.votamas.model.auth.gateways.UserRoleRepository;
import com.votamas.model.exception.BusinessException;
import com.votamas.model.exception.MessageError;
import com.votamas.model.potentialvoter.PotentialVoterSearchCriteria;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class PotentialVoterAccessUseCase {
    private static final String ADMIN_ROLE = "ADMINISTRADOR";

    private final UserRoleRepository userRoleRepository;

    public Mono<PotentialVoterSearchCriteria> scope(
            PotentialVoterSearchCriteria criteria, UUID authenticatedUserId) {
        return isAdministrator(authenticatedUserId)
                .map(isAdministrator -> isAdministrator
                        ? criteria
                        : criteria.assignedTo(authenticatedUserId));
    }

    public Mono<Void> verifyOwnership(UUID authenticatedUserId, UUID assignedLeaderId) {
        return isAdministrator(authenticatedUserId)
                .filter(isAdministrator -> isAdministrator
                        || authenticatedUserId.equals(assignedLeaderId))
                .switchIfEmpty(Mono.error(new BusinessException(MessageError.ACCESS_DENIED)))
                .then();
    }

    private Mono<Boolean> isAdministrator(UUID userId) {
        return userRoleRepository.hasActiveRole(userId, ADMIN_ROLE);
    }
}
