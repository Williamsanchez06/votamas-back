package com.votamas.usecase.potentialvoter;

import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.model.potentialvoter.PotentialVoterDetails;
import com.votamas.model.potentialvoter.gateways.PotentialVoterQueryRepository;
import com.votamas.model.potentialvoter.gateways.PotentialVoterRepository;
import com.votamas.model.potentialvoter.gateways.VotingLocationRepository;
import com.votamas.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PotentialVoterUseCaseTest {
    private final PotentialVoterRepository voterRepository = mock(PotentialVoterRepository.class);
    private final PotentialVoterQueryRepository queryRepository =
            mock(PotentialVoterQueryRepository.class);
    private final VotingLocationRepository locationRepository =
            mock(VotingLocationRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PotentialVoterUseCase useCase = new PotentialVoterUseCase(
            voterRepository,
            queryRepository,
            locationRepository,
            userRepository,
            mock(PotentialVoterAccessUseCase.class)
    );

    @Test
    void shouldReturnAssignedLeaderNameAfterCreatingPotentialVoter() {
        UUID voterId = UUID.randomUUID();
        UUID leaderId = UUID.randomUUID();
        UUID votingTableId = UUID.randomUUID();
        var voter = PotentialVoter.builder()
                .identification("123456")
                .firstName("Ana")
                .lastName("Pérez")
                .assignedLeaderId(leaderId)
                .votingTableId(votingTableId)
                .build();
        var saved = voter.toBuilder().id(voterId).build();
        var details = PotentialVoterDetails.builder()
                .id(voterId)
                .identification(voter.identification())
                .firstName(voter.firstName())
                .lastName(voter.lastName())
                .assignedLeaderName("Laura Gómez")
                .build();

        when(voterRepository.existsByIdentification(voter.identification()))
                .thenReturn(Mono.just(false));
        when(userRepository.isActiveById(leaderId)).thenReturn(Mono.just(true));
        when(locationRepository.existsVotingTableById(votingTableId)).thenReturn(Mono.just(true));
        when(voterRepository.save(voter)).thenReturn(Mono.just(saved));
        when(queryRepository.findByIdWithVotingLocation(voterId)).thenReturn(Mono.just(details));

        StepVerifier.create(useCase.savePotentialVoter(voter))
                .expectNextMatches(result ->
                        result.id().equals(voterId)
                                && result.assignedLeaderName().equals("Laura Gómez"))
                .verifyComplete();

        verify(queryRepository).findByIdWithVotingLocation(voterId);
    }
}
