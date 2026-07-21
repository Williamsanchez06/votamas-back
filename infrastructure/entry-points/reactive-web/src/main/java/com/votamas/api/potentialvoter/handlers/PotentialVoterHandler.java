package com.votamas.api.potentialvoter.handlers;

import com.votamas.api.potentialvoter.dtos.PotentialVoterCreateRequestDTO;
import com.votamas.api.potentialvoter.dtos.PotentialVoterUpdateRequestDTO;
import com.votamas.api.potentialvoter.mappers.PotentialVoterMapper;
import com.votamas.api.common.web.PaginationRequestParser;
import com.votamas.api.common.web.PathVariableParser;
import com.votamas.api.common.validation.RequestValidator;
import com.votamas.api.common.web.PotentialVoterImportRequestExtractor;
import com.votamas.api.common.web.AuthenticatedUserIdResolver;
import com.votamas.usecase.potentialvoter.ImportPotentialVotersUseCase;
import com.votamas.usecase.potentialvoter.PotentialVoterUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
        var pageRequest = PaginationRequestParser.from(request);
        return potentialVoterUseCase.getAllPotentialVoters(pageRequest)
                .map(result -> result.map(potentialVoterMapper::toResponse))
                .flatMap(result -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(result));
    }

    public Mono<ServerResponse> updatePotentialVoter(ServerRequest request) {
        UUID id = PathVariableParser.uuid(request.pathVariable("id"), "id");

        return requestValidator.body(request, PotentialVoterUpdateRequestDTO.class)
                .map(potentialVoterMapper::toPotentialVoter)
                .flatMap(pv -> potentialVoterUseCase.updatePotentialVoter(id, pv))
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

}
