package com.votamas.usecase.potentialvoter;

import com.votamas.model.exception.ConflictException;
import com.votamas.model.exception.MessageError;
import com.votamas.model.exception.NotFoundException;
import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.model.potentialvoter.gateways.PotentialVoterRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class PotentialVoterUseCase {

    private final PotentialVoterRepository potentialVoterRepository;

    public Mono<PotentialVoter> savePotentialVoter(PotentialVoter potentialVoter) {
        return potentialVoterRepository.existsByIdentification(potentialVoter.identification())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ConflictException(MessageError.ID_ALREADY_REGISTERED));
                    }
                    return potentialVoterRepository.save(potentialVoter);
                });
    }

    public Flux<PotentialVoter> getAllPotentialVoters() {
        return potentialVoterRepository.findAll();
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
