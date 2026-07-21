package com.votamas.r2dbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.r2dbc")
public record PostgresqlConnectionProperties(
        String host,
        Integer port,
        String database,
        String schema,
        String username,
        String password,
        boolean ssl,
        int initialSize,
        int maxSize,
        int maxIdleTimeMinutes,
        int maxAcquireTimeSeconds) {
}
