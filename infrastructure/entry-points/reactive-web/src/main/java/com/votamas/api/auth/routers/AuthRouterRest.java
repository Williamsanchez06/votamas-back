package com.votamas.api.auth.routers;

import com.votamas.api.auth.handlers.AuthHandler;
import com.votamas.api.config.ApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class AuthRouterRest {
    private final ApiProperties apiProperties;

    @Bean
    public RouterFunction<ServerResponse> authRouterFunction(AuthHandler handler) {
        return route()
                .POST(apiProperties.baseApiPath.concat("/auth/login"), handler::login)
                .build();
    }
}
