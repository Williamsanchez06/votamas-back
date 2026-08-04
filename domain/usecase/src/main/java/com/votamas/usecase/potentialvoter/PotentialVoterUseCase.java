package com.votamas.usecase.potentialvoter;

import com.votamas.model.common.pagination.PageResult;
import com.votamas.model.exception.ConflictException;
import com.votamas.model.exception.MessageError;
import com.votamas.model.exception.NotFoundException;
import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.model.potentialvoter.PotentialVoterDetails;
import com.votamas.model.potentialvoter.PotentialVoterSearchCriteria;
import com.votamas.model.potentialvoter.gateways.PotentialVoterQueryRepository;
import com.votamas.model.potentialvoter.gateways.PotentialVoterRepository;
import com.votamas.model.potentialvoter.gateways.VotingLocationRepository;
import com.votamas.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class PotentialVoterUseCase {

    private final PotentialVoterRepository potentialVoterRepository;
    private final PotentialVoterQueryRepository potentialVoterQueryRepository;
    private final VotingLocationRepository votingLocationRepository;
    private final UserRepository userRepository;
    private final PotentialVoterAccessUseCase potentialVoterAccessUseCase;

    public Mono<PotentialVoterDetails> savePotentialVoter(PotentialVoter potentialVoter) {
        return Mono.zip(
                        potentialVoterRepository.existsByIdentification(potentialVoter.identification()),
                        userRepository.isActiveById(potentialVoter.assignedLeaderId()),
                        votingLocationRepository.existsVotingTableById(potentialVoter.votingTableId()))
                .flatMap(validation -> {
                    if (validation.getT1()) {
                        return Mono.error(new ConflictException(MessageError.ID_ALREADY_REGISTERED));
                    }
                    if (!validation.getT2()) {
                        return Mono.error(new NotFoundException(MessageError.NO_USER_FOUND));
                    }
                    if (!validation.getT3()) {
                        return Mono.error(new NotFoundException(MessageError.NO_VOTING_TABLE_FOUND));
                    }
                    return potentialVoterRepository.save(potentialVoter);
                })
                .flatMap(this::findDetails);
    }

    public Mono<PageResult<PotentialVoterDetails>> getAllPotentialVoters(
            PotentialVoterSearchCriteria criteria, UUID authenticatedUserId) {
        return potentialVoterAccessUseCase.scope(criteria, authenticatedUserId)
                .flatMap(potentialVoterQueryRepository::findAllWithVotingLocation);
    }

    public Mono<PotentialVoterDetails> updatePotentialVoter(
            UUID id, PotentialVoter potentialVoter, UUID authenticatedUserId) {
        return potentialVoterRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(MessageError.NO_POTENTIAL_VOTER_FOUND)))
                .flatMap(existing -> potentialVoterAccessUseCase
                        .verifyOwnership(authenticatedUserId, existing.assignedLeaderId())
                        .then(votingLocationRepository
                        .existsVotingTableById(potentialVoter.votingTableId())
                        .filter(Boolean.TRUE::equals)
                        .switchIfEmpty(Mono.error(new NotFoundException(MessageError.NO_VOTING_TABLE_FOUND)))
                        .then(Mono.defer(() -> {
                    PotentialVoter updated = existing.toBuilder()
                            .firstName(potentialVoter.firstName())
                            .lastName(potentialVoter.lastName())
                            .votingTableId(potentialVoter.votingTableId())
                            .assignedLeaderId(existing.assignedLeaderId())
                            .build();
                    return potentialVoterRepository.save(updated);
                        }))))
                .flatMap(this::findDetails);
    }

    private Mono<PotentialVoterDetails> findDetails(PotentialVoter potentialVoter) {
        return potentialVoterQueryRepository.findByIdWithVotingLocation(potentialVoter.id())
                .switchIfEmpty(Mono.error(
                        new NotFoundException(MessageError.NO_POTENTIAL_VOTER_FOUND)));
    }
}
