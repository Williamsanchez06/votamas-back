package com.votamas.api.potentialvoter;

import com.votamas.api.potentialvoter.handlers.VotingLocationHandler;
import com.votamas.api.potentialvoter.routers.VotingLocationRouterRest;
import com.votamas.api.utils.ApiProperties;
import com.votamas.model.potentialvoter.VotingZone;
import com.votamas.model.potentialvoter.VotingZoneDetails;
import com.votamas.usecase.potentialvoter.VotingLocationUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VotingLocationRouterRestTest {

    @Test
    void shouldExposeZoneCatalogAndZoneDetails() {
        UUID zoneId = UUID.randomUUID();
        VotingLocationUseCase useCase = mock(VotingLocationUseCase.class);
        when(useCase.getVotingZones()).thenReturn(Flux.just(new VotingZone(zoneId, "Zona 01")));
        when(useCase.getVotingZone(zoneId)).thenReturn(Mono.just(new VotingZoneDetails(
                zoneId, "Zona 01", List.of(new VotingZoneDetails.PollingPlaceDetails(
                UUID.randomUUID(), "Colegio A", List.of(
                new VotingZoneDetails.VotingTableDetails(UUID.randomUUID(), 1)))))));
        var handler = new VotingLocationHandler(useCase);
        var router = new VotingLocationRouterRest(new ApiProperties("/api/v1"))
                .votingLocationRouterFunction(handler);
        WebTestClient client = WebTestClient.bindToRouterFunction(router).build();

        client.get().uri("/api/v1/voting-zones")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(zoneId.toString())
                .jsonPath("$[0].name").isEqualTo("Zona 01");

        client.get().uri("/api/v1/voting-zones/{id}", zoneId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pollingPlaces[0].votingTables[0].tableNumber").isEqualTo(1);
    }
}
