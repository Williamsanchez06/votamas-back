package com.votamas.api.potentialvoter.handlers;

import com.votamas.api.potentialvoter.dtos.PotentialVoterRequestDTO;
import com.votamas.api.potentialvoter.mappers.PotentialVoterMapper;
import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.usecase.potentialvoter.PotentialVoterUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.util.UUID;

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
        return ServerResponse.ok()
                .body(potentialVoterUseCase.getAllPotentialVoters(), PotentialVoter.class);
    }

    public Mono<ServerResponse> updatePotentialVoter(ServerRequest request) {
        UUID id = UUID.fromString(request.pathVariable("id"));

        return request.bodyToMono(PotentialVoterRequestDTO.class)
                .map(PotentialVoterMapper.INSTANCE::toPotentialVoter)
                .flatMap(pv -> potentialVoterUseCase.updatePotentialVoter(id, pv))
                .flatMap(pv -> ServerResponse.ok().bodyValue(pv));
    }
}
