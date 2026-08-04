package com.votamas.r2dbc.potentialvoter.mapper;

import com.votamas.r2dbc.potentialvoter.projections.PotentialVoterDetailsProjection;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PotentialVoterDetailsMapperTest {

    private final PotentialVoterDetailsMapper mapper =
            Mappers.getMapper(PotentialVoterDetailsMapper.class);

    @Test
    void shouldMapCompleteVotingLocation() {
        UUID tableId = UUID.randomUUID();
        UUID placeId = UUID.randomUUID();
        UUID zoneId = UUID.randomUUID();
        var source = projection(tableId, placeId, zoneId);

        var voter = mapper.toDomain(source);

        assertEquals(tableId, voter.votingTable().id());
        assertEquals(10, voter.votingTable().tableNumber());
        assertEquals(placeId, voter.votingTable().pollingPlace().id());
        assertEquals("Colegio Central", voter.votingTable().pollingPlace().name());
        assertEquals(zoneId, voter.votingTable().pollingPlace().votingZone().id());
        assertEquals("Zona 01", voter.votingTable().pollingPlace().votingZone().name());
        assertEquals("Laura Gómez", voter.assignedLeaderName());
    }

    @Test
    void shouldReturnNullVotingTableWhenRelationIsMissing() {
        var voter = mapper.toDomain(projection(null, null, null));

        assertEquals("123456", voter.identification());
        assertNull(voter.votingTable());
    }

    private PotentialVoterDetailsProjection projection(UUID tableId, UUID placeId, UUID zoneId) {
        return new PotentialVoterDetailsProjection(
                UUID.randomUUID(), "123456", "Ana", "Pérez", LocalDate.of(2026, 7, 19),
                "Laura Gómez", tableId, tableId == null ? null : 10, placeId,
                placeId == null ? null : "Colegio Central", zoneId, zoneId == null ? null : "Zona 01"
        );
    }
}
