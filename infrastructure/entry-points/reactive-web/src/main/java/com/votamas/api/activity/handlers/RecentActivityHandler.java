package com.votamas.api.activity.handlers;

import com.votamas.api.activity.dtos.RecentActivityResponseDTO;
import com.votamas.api.activity.mappers.RecentActivityMapper;
import com.votamas.api.common.validation.InvalidRequestException;
import com.votamas.usecase.activity.RecentActivityUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RecentActivityHandler {
    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 50;

    private final RecentActivityUseCase recentActivityUseCase;
    private final RecentActivityMapper recentActivityMapper;

    public Mono<ServerResponse> getRecentActivity(ServerRequest request) {
        int limit = parseLimit(request);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(recentActivityUseCase.execute(limit).map(recentActivityMapper::toResponse),
                        RecentActivityResponseDTO.class);
    }

    private int parseLimit(ServerRequest request) {
        String value = request.queryParam("limit").orElse(Integer.toString(DEFAULT_LIMIT));
        try {
            int limit = Integer.parseInt(value);
            if (limit < 1 || limit > MAX_LIMIT) {
                throw invalidLimit();
            }
            return limit;
        } catch (NumberFormatException exception) {
            throw invalidLimit();
        }
    }

    private InvalidRequestException invalidLimit() {
        return InvalidRequestException.forField(
                "limit", "El límite debe ser un número entero entre 1 y " + MAX_LIMIT);
    }
}
