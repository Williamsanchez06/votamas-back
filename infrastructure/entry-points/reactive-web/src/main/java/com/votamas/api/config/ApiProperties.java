package com.votamas.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ApiProperties {
    public final String baseApiPath;

    public ApiProperties(
            @Value("${entries.reactive-web.base-path-api}") String baseApiPath
    ) {
        this.baseApiPath = baseApiPath;
    }
}