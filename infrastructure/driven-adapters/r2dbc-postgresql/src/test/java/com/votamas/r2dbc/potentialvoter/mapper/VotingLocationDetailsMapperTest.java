package com.votamas.r2dbc.potentialvoter.mapper;

import com.votamas.r2dbc.potentialvoter.projections.VotingLocationProjection;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VotingLocationDetailsMapperTest {
    private final VotingLocationDetailsMapper mapper = new VotingLocationDetailsMapper();

    @Test
    void shouldGroupMultiplePlacesAndTables() {
        UUID zoneId = UUID.randomUUID();
        UUID firstPlace = UUID.randomUUID();
        UUID secondPlace = UUID.randomUUID();
        List<VotingLocationProjection> rows = List.of(
                row(zoneId, firstPlace, "Colegio A", UUID.randomUUID(), 1),
                row(zoneId, firstPlace, "Colegio A", UUID.randomUUID(), 2),
                row(zoneId, secondPlace, "Colegio B", UUID.randomUUID(), 1)
        );

        var result = mapper.toDomain(rows);

        assertEquals(zoneId, result.id());
        assertEquals(2, result.pollingPlaces().size());
        assertEquals(2, result.pollingPlaces().getFirst().votingTables().size());
        assertEquals(1, result.pollingPlaces().get(1).votingTables().size());
    }

    @Test
    void shouldIncludePlaceWithoutTables() {
        UUID zoneId = UUID.randomUUID();
        var result = mapper.toDomain(List.of(
                row(zoneId, UUID.randomUUID(), "Colegio sin mesas", null, null)));

        assertEquals(1, result.pollingPlaces().size());
        assertTrue(result.pollingPlaces().getFirst().votingTables().isEmpty());
    }

    @Test
    void shouldReturnEmptyPlacesWhenZoneHasNone() {
        UUID zoneId = UUID.randomUUID();
        var result = mapper.toDomain(List.of(
                row(zoneId, null, null, null, null)));

        assertEquals(zoneId, result.id());
        assertTrue(result.pollingPlaces().isEmpty());
    }

    private VotingLocationProjection row(UUID zoneId, UUID placeId, String placeName,
                                         UUID tableId, Integer tableNumber) {
        return new VotingLocationProjection(zoneId, "Zona 01", placeId, placeName, tableId, tableNumber);
    }
}
