package com.votamas.r2dbc.potentialvoter;

import com.votamas.r2dbc.potentialvoter.mapper.VotingLocationDetailsMapper;
import com.votamas.r2dbc.potentialvoter.projections.VotingLocationProjection;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VotingLocationAdapterTest {

    @Test
    void shouldLoadZoneHierarchyWithOneDatabaseQuery() {
        UUID zoneId = UUID.randomUUID();
        DatabaseClient client = mock(DatabaseClient.class);
        DatabaseClient.GenericExecuteSpec executeSpec = mock(DatabaseClient.GenericExecuteSpec.class);
        @SuppressWarnings("unchecked")
        RowsFetchSpec<VotingLocationProjection> rows = mock(RowsFetchSpec.class);
        when(client.sql(anyString())).thenReturn(executeSpec);
        when(executeSpec.bind("zoneId", zoneId)).thenReturn(executeSpec);
        when(executeSpec.mapProperties(VotingLocationProjection.class)).thenReturn(rows);
        when(rows.all()).thenReturn(Flux.just(
                new VotingLocationProjection(zoneId, "Zona 01", UUID.randomUUID(),
                        "Colegio A", UUID.randomUUID(), 1),
                new VotingLocationProjection(zoneId, "Zona 01", UUID.randomUUID(),
                        "Colegio B", UUID.randomUUID(), 1)));
        var adapter = new VotingLocationAdapter(client, new VotingLocationDetailsMapper());

        StepVerifier.create(adapter.findZoneDetailsById(zoneId))
                .expectNextMatches(zone -> zone.pollingPlaces().size() == 2)
                .verifyComplete();

        verify(client, times(1)).sql(anyString());
    }
}
