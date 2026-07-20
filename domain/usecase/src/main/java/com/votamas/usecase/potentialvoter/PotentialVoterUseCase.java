package com.votamas.usecase.potentialvoter;

import com.votamas.model.common.pagination.PageRequest;
import com.votamas.model.common.pagination.PageResult;
import com.votamas.model.exception.ConflictException;
import com.votamas.model.exception.MessageError;
import com.votamas.model.exception.NotFoundException;
import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.model.potentialvoter.PotentialVoterDetails;
import com.votamas.model.potentialvoter.gateways.PotentialVoterQueryRepository;
import com.votamas.model.potentialvoter.gateways.PotentialVoterRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class PotentialVoterUseCase {

    private final PotentialVoterRepository potentialVoterRepository;
    private final PotentialVoterQueryRepository potentialVoterQueryRepository;

    public Mono<PotentialVoter> savePotentialVoter(PotentialVoter potentialVoter) {
        return potentialVoterRepository.existsByIdentification(potentialVoter.identification())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ConflictException(MessageError.ID_ALREADY_REGISTERED));
                    }
                    return potentialVoterRepository.save(potentialVoter);
                });
    }

    public Mono<PageResult<PotentialVoterDetails>> getAllPotentialVoters(PageRequest pageRequest) {
        return potentialVoterQueryRepository.findAllWithVotingLocation(pageRequest);
    }

    public Mono<PotentialVoter> updatePotentialVoter(UUID id, PotentialVoter potentialVoter) {
        return potentialVoterRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(MessageError.NO_POTENTIAL_VOTER_FOUND)))
                .flatMap(existing -> {
                    PotentialVoter updated = existing.toBuilder()
                            .firstName(potentialVoter.firstName())
                            .lastName(potentialVoter.lastName())
                            .votingTableId(potentialVoter.votingTableId())
                            .assignedLeaderId(potentialVoter.assignedLeaderId())
                            .build();
                    return potentialVoterRepository.save(updated);
                });
    }
}
