package com.votamas.usecase.potentialvoter;

import com.votamas.model.exception.ConflictException;
import com.votamas.model.exception.MessageError;
import com.votamas.model.exception.NotFoundException;
import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.model.potentialvoter.PotentialVoterImportData;
import com.votamas.model.potentialvoter.PotentialVoterImportError;
import com.votamas.model.potentialvoter.PotentialVoterImportResult;
import com.votamas.model.potentialvoter.PotentialVoterImportRow;
import com.votamas.model.potentialvoter.VotingTableLookupKey;
import com.votamas.model.potentialvoter.VotingTableReference;
import com.votamas.model.potentialvoter.gateways.PotentialVoterRepository;
import com.votamas.model.potentialvoter.gateways.PotentialVoterSpreadsheetReader;
import com.votamas.model.potentialvoter.gateways.VotingLocationRepository;
import com.votamas.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .filter(user -> Boolean.TRUE.equals(user.active()))
                .switchIfEmpty(Mono.error(new NotFoundException(MessageError.NO_USER_FOUND)))
                .then(Mono.defer(() -> spreadsheetReader.read(content)))
                .flatMap(data -> importRows(data, assignedLeaderId));
    }

    private Mono<PotentialVoterImportResult> importRows(PotentialVoterImportData data, UUID leaderId) {
        Set<String> requestedIdentifications = data.rows().stream()
                .map(this::normalize)
                .map(NormalizedRow::identification)
                .filter(identification -> !identification.isBlank())
                .collect(Collectors.toSet());

        Mono<Set<String>> existingIdentifications = potentialVoterRepository
                .findExistingIdentifications(requestedIdentifications)
                .collect(Collectors.toSet());
        Mono<Map<VotingTableLookupKey, UUID>> votingTables = votingLocationRepository
                .findAllVotingTableReferences()
                .collectMap(this::lookupKey, VotingTableReference::id);

        return Mono.zip(existingIdentifications, votingTables)
                .flatMap(context -> processRows(data, leaderId, context.getT1(), context.getT2()));
    }

    private Mono<PotentialVoterImportResult> processRows(
            PotentialVoterImportData data,
            UUID leaderId,
            Set<String> existingIdentifications,
            Map<VotingTableLookupKey, UUID> votingTables) {
        Set<String> fileIdentifications = new HashSet<>();
        return Flux.fromIterable(data.rows())
                .concatMap(row -> processRow(row, leaderId, fileIdentifications,
                        existingIdentifications, votingTables))
                .collectList()
                .map(errors -> new PotentialVoterImportResult(
                        data.rows().size(), data.rows().size() - errors.size(), errors.size(),
                        data.skippedRows(), errors));
    }

    private Mono<PotentialVoterImportError> processRow(
            PotentialVoterImportRow row,
            UUID leaderId,
            Set<String> fileIdentifications,
            Set<String> existingIdentifications,
            Map<VotingTableLookupKey, UUID> votingTables) {
        NormalizedRow normalized = normalize(row);
        List<String> validationErrors = validate(normalized);
        if (!validationErrors.isEmpty()) {
            return Mono.just(error(row, normalized.identification(), String.join("; ", validationErrors)));
        }
        if (!fileIdentifications.add(normalized.identification())) {
            return Mono.just(error(row, normalized.identification(),
                    "La identificación está duplicada dentro del archivo"));
        }
        if (existingIdentifications.contains(normalized.identification())) {
            return Mono.just(error(row, normalized.identification(),
                    "La identificación ya se encuentra registrada"));
        }

        int tableNumber = Integer.parseInt(normalized.tableNumber());
        VotingTableLookupKey key = new VotingTableLookupKey(
                canonical(normalized.votingZoneName()),
                canonical(normalized.pollingPlaceName()),
                tableNumber);
        UUID tableId = votingTables.get(key);
        if (tableId == null) {
            return Mono.just(tableNotFound(row, normalized, tableNumber));
        }

        return save(normalized, tableId, leaderId)
                .then(Mono.<PotentialVoterImportError>empty())
                .onErrorResume(ConflictException.class, conflict -> Mono.just(error(
                        row, normalized.identification(), conflict.getMessage())));
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
                clean(row.votingZoneName()),
                clean(row.pollingPlaceName()), clean(row.tableNumber()));
    }

    private String clean(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    private String canonical(String value) {
        return value.toLowerCase(Locale.ROOT);
    }

    private VotingTableLookupKey lookupKey(VotingTableReference reference) {
        return new VotingTableLookupKey(canonical(reference.votingZoneName()),
                canonical(reference.pollingPlaceName()), reference.tableNumber());
    }

    private PotentialVoterImportError error(PotentialVoterImportRow row, String identification, String message) {
        return new PotentialVoterImportError(row.rowNumber(), identification, message);
    }

    private record NormalizedRow(String identification, String firstName, String lastName,
                                 String votingZoneName,
                                 String pollingPlaceName, String tableNumber) {
    }
}
