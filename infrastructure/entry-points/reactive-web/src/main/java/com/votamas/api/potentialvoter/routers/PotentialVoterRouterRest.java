package com.votamas.api.potentialvoter.routers;

import com.votamas.api.potentialvoter.handlers.PotentialVoterHandler;
import com.votamas.api.config.ApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class PotentialVoterRouterRest {

    private final ApiProperties apiProperties;

    @Bean
    public RouterFunction<ServerResponse> potentialVoterRouterFunction(PotentialVoterHandler handler) {
        return RouterFunctions.route()
                .GET(apiProperties.baseApiPath.concat("/potential-voter"), handler::getAllPotentialVoters)
                .GET(apiProperties.baseApiPath.concat("/potential-voter/export"),
                        handler::exportPotentialVoters)
                .POST(apiProperties.baseApiPath.concat("/potential-voter/import"), handler::importPotentialVoters)
                .POST(apiProperties.baseApiPath.concat("/potential-voter"), handler::createPotentialVoter)
                .PUT(apiProperties.baseApiPath.concat("/potential-voter/{id}"), handler::updatePotentialVoter)
                .build();
    }
}
