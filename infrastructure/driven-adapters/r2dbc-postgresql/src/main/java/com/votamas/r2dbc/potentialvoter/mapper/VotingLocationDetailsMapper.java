package com.votamas.r2dbc.potentialvoter.mapper;

import com.votamas.model.potentialvoter.VotingZoneDetails;
import com.votamas.r2dbc.potentialvoter.projections.VotingLocationProjection;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class VotingLocationDetailsMapper {

    public VotingZoneDetails toDomain(List<VotingLocationProjection> rows) {
        VotingLocationProjection zone = rows.getFirst();
        Map<UUID, PollingPlaceAccumulator> places = new LinkedHashMap<>();

        rows.stream()
                .filter(row -> row.pollingPlaceId() != null)
                .forEach(row -> places
                        .computeIfAbsent(row.pollingPlaceId(), id ->
                                new PollingPlaceAccumulator(id, row.pollingPlaceName()))
                        .addTable(row));

        List<VotingZoneDetails.PollingPlaceDetails> pollingPlaces = places.values().stream()
                .map(PollingPlaceAccumulator::toDomain)
                .toList();

        return new VotingZoneDetails(zone.votingZoneId(), zone.votingZoneName(), pollingPlaces);
    }

    private static final class PollingPlaceAccumulator {
        private final UUID id;
        private final String name;
        private final List<VotingZoneDetails.VotingTableDetails> tables = new ArrayList<>();

        private PollingPlaceAccumulator(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        private void addTable(VotingLocationProjection row) {
            if (row.votingTableId() != null) {
                tables.add(new VotingZoneDetails.VotingTableDetails(
                        row.votingTableId(), row.tableNumber()));
            }
        }

        private VotingZoneDetails.PollingPlaceDetails toDomain() {
            return new VotingZoneDetails.PollingPlaceDetails(id, name, tables);
        }
    }
}
