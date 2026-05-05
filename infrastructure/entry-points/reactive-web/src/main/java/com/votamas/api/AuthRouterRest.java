package com.votamas.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AuthRouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(AuthHandler authHandler) {
        return route(GET("/api/usecase/path"), authHandler::listenGETUseCase)
                .andRoute(POST("/api/usecase/otherpath"), authHandler::listenPOSTUseCase)
                .and(route(GET("/api/otherusercase/path"), authHandler::listenGETOtherUseCase));
    }
}
