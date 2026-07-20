package com.votamas.usecase.potentialvoter;

import com.votamas.model.exception.NotFoundException;
import com.votamas.model.potentialvoter.VotingZone;
import com.votamas.model.potentialvoter.VotingZoneDetails;
import com.votamas.model.potentialvoter.gateways.VotingLocationRepository;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VotingLocationUseCaseTest {

    @Test
    void shouldReturnAllZones() {
        VotingLocationRepository repository = mock(VotingLocationRepository.class);
        var useCase = new VotingLocationUseCase(repository);
        when(repository.findAllZones()).thenReturn(Flux.just(
                new VotingZone(UUID.randomUUID(), "Zona 01"),
                new VotingZone(UUID.randomUUID(), "Zona 02")));

        StepVerifier.create(useCase.getVotingZones())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void shouldReturnZoneWithoutPollingPlaces() {
        VotingLocationRepository repository = mock(VotingLocationRepository.class);
        var useCase = new VotingLocationUseCase(repository);
        UUID zoneId = UUID.randomUUID();
        when(repository.findZoneDetailsById(zoneId))
                .thenReturn(Mono.just(new VotingZoneDetails(zoneId, "Zona 01", List.of())));

        StepVerifier.create(useCase.getVotingZone(zoneId))
                .expectNextMatches(zone -> zone.pollingPlaces().isEmpty())
                .verifyComplete();
    }

    @Test
    void shouldFailWhenZoneDoesNotExist() {
        VotingLocationRepository repository = mock(VotingLocationRepository.class);
        var useCase = new VotingLocationUseCase(repository);
        UUID zoneId = UUID.randomUUID();
        when(repository.findZoneDetailsById(zoneId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getVotingZone(zoneId))
                .expectError(NotFoundException.class)
                .verify();
    }
}
