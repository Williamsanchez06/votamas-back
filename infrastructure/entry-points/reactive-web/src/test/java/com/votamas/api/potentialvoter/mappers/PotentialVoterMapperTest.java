package com.votamas.api.potentialvoter.mappers;

import com.votamas.model.potentialvoter.PotentialVoterDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PotentialVoterMapperTest {
    private final PotentialVoterMapper mapper = Mappers.getMapper(PotentialVoterMapper.class);

    @Test
    void shouldExposeAssignedLeaderName() {
        var voter = PotentialVoterDetails.builder()
                .id(UUID.randomUUID())
                .identification("123456")
                .firstName("Ana")
                .lastName("Pérez")
                .assignedLeaderName("Laura Gómez")
                .build();

        var response = mapper.toResponse(voter);

        assertThat(response.assignedLeaderName()).isEqualTo("Laura Gómez");
    }
}
