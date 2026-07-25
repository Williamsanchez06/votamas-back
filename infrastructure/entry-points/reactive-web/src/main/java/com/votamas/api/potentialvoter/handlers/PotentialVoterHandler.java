package com.votamas.api.potentialvoter.handlers;

import com.votamas.api.potentialvoter.dtos.PotentialVoterCreateRequestDTO;
import com.votamas.api.potentialvoter.dtos.PotentialVoterUpdateRequestDTO;
import com.votamas.api.potentialvoter.config.PotentialVoterExportProperties;
import com.votamas.api.potentialvoter.mappers.PotentialVoterMapper;
import com.votamas.api.potentialvoter.search.PotentialVoterSearchCriteriaParser;
import com.votamas.api.common.web.PathVariableParser;
import com.votamas.api.common.validation.RequestValidator;
import com.votamas.api.common.web.PotentialVoterImportRequestExtractor;
import com.votamas.api.common.web.AuthenticatedUserIdResolver;
import com.votamas.usecase.potentialvoter.ImportPotentialVotersUseCase;
import com.votamas.usecase.potentialvoter.ExportPotentialVotersUseCase;
import com.votamas.usecase.potentialvoter.PotentialVoterUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@RequiredArgsConstructor
public class PotentialVoterHandler {

    private final PotentialVoterUseCase potentialVoterUseCase;
    private final RequestValidator requestValidator;
    private final PotentialVoterImportRequestExtractor importRequestExtractor;
    private final ImportPotentialVotersUseCase importPotentialVotersUseCase;
    private final ExportPotentialVotersUseCase exportPotentialVotersUseCase;
    private final PotentialVoterExportProperties exportProperties;
    private final AuthenticatedUserIdResolver authenticatedUserIdResolver;
    private final PotentialVoterMapper potentialVoterMapper;

    public Mono<ServerResponse> createPotentialVoter(ServerRequest request) {
        return requestValidator.body(request, PotentialVoterCreateRequestDTO.class)
                .map(potentialVoterMapper::toPotentialVoter)
                .zipWhen(ignored -> authenticatedUserIdResolver.resolve(request))
                .map(tuple -> tuple.getT1().toBuilder().assignedLeaderId(tuple.getT2()).build())
                .flatMap(potentialVoterUseCase::savePotentialVoter)
                .map(potentialVoterMapper::toResponse)
                .flatMap(pv -> ServerResponse.status(201).bodyValue(pv));
    }

    public Mono<ServerResponse> getAllPotentialVoters(ServerRequest request) {
        var criteria = PotentialVoterSearchCriteriaParser.from(request);
        return authenticatedUserIdResolver.resolve(request)
                .flatMap(userId -> potentialVoterUseCase.getAllPotentialVoters(criteria, userId))
                .map(result -> result.map(potentialVoterMapper::toResponse))
                .flatMap(result -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(result));
    }

    public Mono<ServerResponse> updatePotentialVoter(ServerRequest request) {
        UUID id = PathVariableParser.uuid(request.pathVariable("id"), "id");

        return requestValidator.body(request, PotentialVoterUpdateRequestDTO.class)
                .map(potentialVoterMapper::toPotentialVoter)
                .zipWhen(ignored -> authenticatedUserIdResolver.resolve(request))
                .flatMap(tuple -> potentialVoterUseCase.updatePotentialVoter(
                        id, tuple.getT1(), tuple.getT2()))
                .map(potentialVoterMapper::toResponse)
                .flatMap(pv -> ServerResponse.ok().bodyValue(pv));
    }

    public Mono<ServerResponse> importPotentialVoters(ServerRequest request) {
        return importRequestExtractor.extract(request)
                .zipWhen(ignored -> authenticatedUserIdResolver.resolve(request))
                .flatMap(tuple -> importPotentialVotersUseCase.execute(
                        tuple.getT1().content(), tuple.getT2()))
                .map(potentialVoterMapper::toResponse)
                .flatMap(result -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(result));
    }

    public Mono<ServerResponse> exportPotentialVoters(ServerRequest request) {
        var criteria = PotentialVoterSearchCriteriaParser.from(request);
        return authenticatedUserIdResolver.resolve(request)
                .flatMap(userId -> exportPotentialVotersUseCase.execute(
                        criteria, exportProperties.maxRows(), userId))
                .flatMap(content -> ServerResponse.ok()
                        .contentType(MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .headers(headers -> headers.setContentDisposition(
                                ContentDisposition.attachment()
                                        .filename("posibles-votantes.xlsx")
                                        .build()))
                        .bodyValue(content));
    }

}
