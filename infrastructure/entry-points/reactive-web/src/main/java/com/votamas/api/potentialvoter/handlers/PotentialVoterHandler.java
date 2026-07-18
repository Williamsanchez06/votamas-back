package com.votamas.api.potentialvoter.handlers;

import com.votamas.api.potentialvoter.dtos.PotentialVoterRequestDTO;
import com.votamas.api.potentialvoter.mappers.PotentialVoterMapper;
import com.votamas.model.common.pagination.PageRequest;
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
        PageRequest pageRequest = new PageRequest(
                queryInt(request, "page", PageRequest.DEFAULT_PAGE),
                queryInt(request, "size", PageRequest.DEFAULT_SIZE)
        );
        return potentialVoterUseCase.getAllPotentialVoters(pageRequest)
                .map(result -> result.map(PotentialVoterMapper.INSTANCE::toResponse))
                .flatMap(result -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(result));
    }

    public Mono<ServerResponse> updatePotentialVoter(ServerRequest request) {
        UUID id = UUID.fromString(request.pathVariable("id"));

        return request.bodyToMono(PotentialVoterRequestDTO.class)
                .map(PotentialVoterMapper.INSTANCE::toPotentialVoter)
                .flatMap(pv -> potentialVoterUseCase.updatePotentialVoter(id, pv))
                .flatMap(pv -> ServerResponse.ok().bodyValue(pv));
    }

    private int queryInt(ServerRequest request, String name, int defaultValue) {
        return request.queryParam(name).map(Integer::parseInt).orElse(defaultValue);
    }
}
