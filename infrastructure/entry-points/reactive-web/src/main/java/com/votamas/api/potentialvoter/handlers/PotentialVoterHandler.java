package com.votamas.api.potentialvoter.handlers;

import com.votamas.api.potentialvoter.dtos.PotentialVoterRequestDTO;
import com.votamas.api.potentialvoter.mappers.PotentialVoterMapper;
import com.votamas.api.utils.PaginationRequestParser;
import com.votamas.api.utils.PathVariableParser;
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

    public Mono<ServerResponse> createPotentialVoter(ServerRequest request) {
        return request.bodyToMono(PotentialVoterRequestDTO.class)
                .map(PotentialVoterMapper.INSTANCE::toPotentialVoter)
                .flatMap(potentialVoterUseCase::savePotentialVoter)
                .flatMap(pv -> ServerResponse.ok().bodyValue(pv));
    }

    public Mono<ServerResponse> getAllPotentialVoters(ServerRequest request) {
        var pageRequest = PaginationRequestParser.from(request);
        return potentialVoterUseCase.getAllPotentialVoters(pageRequest)
                .map(result -> result.map(PotentialVoterMapper.INSTANCE::toResponse))
                .flatMap(result -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(result));
    }

    public Mono<ServerResponse> updatePotentialVoter(ServerRequest request) {
        UUID id = PathVariableParser.uuid(request.pathVariable("id"));

        return request.bodyToMono(PotentialVoterRequestDTO.class)
                .map(PotentialVoterMapper.INSTANCE::toPotentialVoter)
                .flatMap(pv -> potentialVoterUseCase.updatePotentialVoter(id, pv))
                .flatMap(pv -> ServerResponse.ok().bodyValue(pv));
    }

}
