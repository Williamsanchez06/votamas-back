package com.votamas.api.potentialvoter.routers;

import com.votamas.api.potentialvoter.handlers.VotingLocationHandler;
import com.votamas.api.config.ApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class VotingLocationRouterRest {
    private final ApiProperties apiProperties;

    @Bean
    public RouterFunction<ServerResponse> votingLocationRouterFunction(VotingLocationHandler handler) {
        String path = apiProperties.baseApiPath.concat("/voting-zones");
        return RouterFunctions.route()
                .GET(path, handler::getVotingZones)
                .GET(path.concat("/{zoneId}"), handler::getVotingZone)
                .build();
    }
}
