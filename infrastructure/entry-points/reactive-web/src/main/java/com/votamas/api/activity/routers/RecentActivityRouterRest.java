package com.votamas.api.activity.routers;

import com.votamas.api.activity.handlers.RecentActivityHandler;
import com.votamas.api.config.ApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class RecentActivityRouterRest {
    private final ApiProperties apiProperties;

    @Bean
    RouterFunction<ServerResponse> recentActivityRouterFunction(RecentActivityHandler handler) {
        return RouterFunctions.route()
                .GET(apiProperties.baseApiPath.concat("/activity/recent"), handler::getRecentActivity)
                .build();
    }
}
