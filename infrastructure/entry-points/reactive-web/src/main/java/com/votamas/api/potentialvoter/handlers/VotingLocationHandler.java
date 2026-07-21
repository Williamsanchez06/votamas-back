package com.votamas.api.potentialvoter.handlers;

import com.votamas.api.potentialvoter.dtos.VotingZoneResponseDTO;
import com.votamas.api.potentialvoter.mappers.VotingLocationMapper;
import com.votamas.api.common.web.PathVariableParser;
import com.votamas.usecase.potentialvoter.VotingLocationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VotingLocationHandler {
    private final VotingLocationUseCase useCase;
    private final VotingLocationMapper votingLocationMapper;

    public Mono<ServerResponse> getVotingZones(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(useCase.getVotingZones().map(votingLocationMapper::toResponse),
                        VotingZoneResponseDTO.class);
    }

    public Mono<ServerResponse> getVotingZone(ServerRequest request) {
        UUID zoneId = PathVariableParser.uuid(request.pathVariable("zoneId"), "zoneId");
        return useCase.getVotingZone(zoneId)
                .map(votingLocationMapper::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

}
