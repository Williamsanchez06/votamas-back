package com.votamas.usecase.potentialvoter;

import com.votamas.model.exception.MessageError;
import com.votamas.model.exception.NotFoundException;
import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.model.potentialvoter.PotentialVoterImportData;
import com.votamas.model.potentialvoter.PotentialVoterImportError;
import com.votamas.model.potentialvoter.PotentialVoterImportResult;
import com.votamas.model.potentialvoter.PotentialVoterImportRow;
import com.votamas.model.potentialvoter.VotingTableLookupKey;
import com.votamas.model.potentialvoter.gateways.PotentialVoterRepository;
import com.votamas.model.potentialvoter.gateways.PotentialVoterSpreadsheetReader;
import com.votamas.model.potentialvoter.gateways.VotingLocationRepository;
import com.votamas.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class ImportPotentialVotersUseCase {
    private static final int MAX_IDENTIFICATION_LENGTH = 30;
    private static final int MAX_NAME_LENGTH = 150;

    private final PotentialVoterSpreadsheetReader spreadsheetReader;
    private final PotentialVoterRepository potentialVoterRepository;
    private final VotingLocationRepository votingLocationRepository;
    private final UserRepository userRepository;

    public Mono<PotentialVoterImportResult> execute(byte[] content, UUID assignedLeaderId) {
        return userRepository.findById(assignedLeaderId)
                .switchIfEmpty(Mono.error(new NotFoundException(MessageError.NO_USER_FOUND)))
                .then(Mono.defer(() -> spreadsheetReader.read(content)))
                .flatMap(data -> importRows(data, assignedLeaderId));
    }

    private Mono<PotentialVoterImportResult> importRows(PotentialVoterImportData data, UUID leaderId) {
        Set<String> identifications = new HashSet<>();
        Map<VotingTableLookupKey, Mono<UUID>> tableCache = new HashMap<>();

        return Flux.fromIterable(data.rows())
                .concatMap(row -> processRow(row, leaderId, identifications, tableCache))
                .collectList()
                .map(errors -> new PotentialVoterImportResult(
                        data.rows().size(),
                        data.rows().size() - errors.size(),
                        errors.size(),
                        data.skippedRows(),
                        errors));
    }

    private Mono<PotentialVoterImportError> processRow(
            PotentialVoterImportRow row,
            UUID leaderId,
            Set<String> identifications,
            Map<VotingTableLookupKey, Mono<UUID>> tableCache) {
        NormalizedRow normalized = normalize(row);
        List<String> validationErrors = validate(normalized);
        if (!validationErrors.isEmpty()) {
            return Mono.just(error(row, normalized.identification(), String.join("; ", validationErrors)));
        }
        if (!identifications.add(normalized.identification())) {
            return Mono.just(error(row, normalized.identification(),
                    "La identificación está duplicada dentro del archivo"));
        }

        int tableNumber = Integer.parseInt(normalized.tableNumber());
        VotingTableLookupKey key = new VotingTableLookupKey(
                canonical(normalized.votingZoneName()),
                canonical(normalized.pollingPlaceName()),
                tableNumber);

        return potentialVoterRepository.existsByIdentification(normalized.identification())
                .flatMap(exists -> exists
                        ? Mono.just(error(row, normalized.identification(),
                                "La identificación ya se encuentra registrada"))
                        : resolveTable(key, normalized, tableCache)
                                .flatMap(tableId -> save(normalized, tableId, leaderId)
                                        .thenReturn(RowOutcome.SUCCESS))
                                .defaultIfEmpty(new RowOutcome(tableNotFound(row, normalized, tableNumber)))
                                .flatMap(outcome -> outcome.error() == null
                                        ? Mono.empty()
                                        : Mono.just(outcome.error())));
    }

    private Mono<UUID> resolveTable(VotingTableLookupKey key, NormalizedRow row,
                                    Map<VotingTableLookupKey, Mono<UUID>> cache) {
        return cache.computeIfAbsent(key, ignored -> votingLocationRepository.findVotingTableId(
                row.votingZoneName(), row.pollingPlaceName(), Integer.parseInt(row.tableNumber())).cache());
    }

    private Mono<PotentialVoter> save(NormalizedRow row, UUID tableId, UUID leaderId) {
        return potentialVoterRepository.save(PotentialVoter.builder()
                .identification(row.identification())
                .firstName(row.firstName())
                .lastName(row.lastName())
                .votingTableId(tableId)
                .assignedLeaderId(leaderId)
                .build());
    }

    private PotentialVoterImportError tableNotFound(PotentialVoterImportRow source, NormalizedRow row,
                                                     int tableNumber) {
        String message = "No se encontró la mesa %d para el lugar de votación \"%s\" en la comuna \"%s\""
                .formatted(tableNumber, row.pollingPlaceName(), row.votingZoneName());
        return error(source, row.identification(), message);
    }

    private List<String> validate(NormalizedRow row) {
        List<String> errors = new ArrayList<>();
        required(row.identification(), "La identificación es obligatoria", errors);
        max(row.identification(), MAX_IDENTIFICATION_LENGTH,
                "La identificación no puede superar 30 caracteres", errors);
        required(row.firstName(), "El nombre es obligatorio", errors);
        max(row.firstName(), MAX_NAME_LENGTH, "El nombre no puede superar 150 caracteres", errors);
        required(row.lastName(), "El apellido es obligatorio", errors);
        max(row.lastName(), MAX_NAME_LENGTH, "El apellido no puede superar 150 caracteres", errors);
        required(row.votingZoneName(), "La comuna es obligatoria", errors);
        required(row.pollingPlaceName(), "El lugar de votación es obligatorio", errors);
        required(row.tableNumber(), "La mesa de votación es obligatoria", errors);
        if (!row.tableNumber().isBlank()) {
            try {
                if (Integer.parseInt(row.tableNumber()) <= 0) {
                    errors.add("La mesa de votación debe ser un número entero positivo");
                }
            } catch (NumberFormatException exception) {
                errors.add("La mesa de votación debe ser un número entero positivo");
            }
        }
        return errors;
    }

    private void required(String value, String message, List<String> errors) {
        if (value.isBlank()) {
            errors.add(message);
        }
    }

    private void max(String value, int max, String message, List<String> errors) {
        if (value.length() > max) {
            errors.add(message);
        }
    }

    private NormalizedRow normalize(PotentialVoterImportRow row) {
        return new NormalizedRow(
                clean(row.identification()), clean(row.firstName()), clean(row.lastName()),
                clean(row.neighborhood()), clean(row.votingZoneName()),
                clean(row.pollingPlaceName()), clean(row.tableNumber()));
    }

    private String clean(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    private String canonical(String value) {
        return value.toLowerCase(Locale.ROOT);
    }

    private PotentialVoterImportError error(PotentialVoterImportRow row, String identification, String message) {
        return new PotentialVoterImportError(row.rowNumber(), identification, message);
    }

    private record NormalizedRow(String identification, String firstName, String lastName,
                                 String neighborhood, String votingZoneName,
                                 String pollingPlaceName, String tableNumber) {
    }

    private record RowOutcome(PotentialVoterImportError error) {
        private static final RowOutcome SUCCESS = new RowOutcome(null);
    }
}
