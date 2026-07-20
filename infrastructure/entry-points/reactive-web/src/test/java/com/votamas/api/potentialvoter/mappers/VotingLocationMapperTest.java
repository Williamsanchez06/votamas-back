package com.votamas.api.potentialvoter.mappers;

import com.votamas.model.potentialvoter.VotingZoneDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VotingLocationMapperTest {
    private final VotingLocationMapper mapper = Mappers.getMapper(VotingLocationMapper.class);

    @Test
    void shouldMapCompleteHttpResponseStructure() {
        var zone = new VotingZoneDetails(
                UUID.randomUUID(),
                "Zona 01",
                List.of(
                        new VotingZoneDetails.PollingPlaceDetails(
                                UUID.randomUUID(), "Colegio A", List.of(
                                new VotingZoneDetails.VotingTableDetails(UUID.randomUUID(), 1),
                                new VotingZoneDetails.VotingTableDetails(UUID.randomUUID(), 2))),
                        new VotingZoneDetails.PollingPlaceDetails(
                                UUID.randomUUID(), "Colegio B", List.of())
                ));

        var response = mapper.toResponse(zone);

        assertEquals("Zona 01", response.name());
        assertEquals(2, response.pollingPlaces().size());
        assertEquals(2, response.pollingPlaces().getFirst().votingTables().size());
        assertTrue(response.pollingPlaces().get(1).votingTables().isEmpty());
    }
}
