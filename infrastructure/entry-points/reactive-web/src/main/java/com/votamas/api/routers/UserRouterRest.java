package com.votamas.api.routers;

import com.votamas.api.handlers.UserHandler;
import com.votamas.api.utils.ApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class UserRouterRest {

    private final ApiProperties apiProperties;

    @Bean
    public RouterFunction<ServerResponse> userRouterFunction(UserHandler userHandler) {

        return RouterFunctions.route()
                .GET(apiProperties.baseApiPath.concat("/user"), userHandler::getAllUsers)
                .POST(apiProperties.baseApiPath.concat("/user"), userHandler::createUser)
                .build();
    }

}
