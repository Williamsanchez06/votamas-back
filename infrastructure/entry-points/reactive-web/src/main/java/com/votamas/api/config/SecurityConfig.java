package com.votamas.api.config;

import com.votamas.api.exceptions.SecurityExceptionHandler;
import com.votamas.model.exception.MessageError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    public static final String PATH_USER = "/api/v1/user/**";
    public static final String PATH_USER_STATUS = "/api/v1/user/*/status";
    public static final String PATH_POTENTIAL_VOTER = "/api/v1/potential-voter/**";

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                     SecurityExceptionHandler exceptionHandler) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((exchange, exception) -> exceptionHandler.handle(
                                exchange, MessageError.AUTHENTICATION_REQUIRED, exception))
                        .accessDeniedHandler((exchange, exception) -> exceptionHandler.handle(
                                exchange, MessageError.ACCESS_DENIED, exception)))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/auth/login", "/actuator/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .pathMatchers(HttpMethod.GET, PATH_USER).hasAuthority("GET_USER")
                        .pathMatchers(HttpMethod.POST, PATH_USER).hasAuthority("CREATE_USER")
                        .pathMatchers(HttpMethod.PUT, PATH_USER).hasAuthority("EDIT_USER")
                        .pathMatchers(HttpMethod.PATCH, PATH_USER_STATUS).hasAuthority("CHANGE_USER_STATUS")
                        .pathMatchers(HttpMethod.PATCH, PATH_USER).hasAuthority("EDIT_USER")
                        .pathMatchers(HttpMethod.GET, PATH_POTENTIAL_VOTER).hasAuthority("GET_POTENTIAL_VOTER")
                        .pathMatchers(HttpMethod.POST, PATH_POTENTIAL_VOTER).hasAuthority("CREATE_POTENTIAL_VOTER")
                        .pathMatchers(HttpMethod.PUT, PATH_POTENTIAL_VOTER).hasAuthority("EDIT_POTENTIAL_VOTER")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .build();
    }

    @Bean
    ReactiveJwtDecoder jwtDecoder(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer
    ) {
        SecretKey key = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withSecretKey(key).build();

        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(Duration.ofSeconds(60)),
                new JwtIssuerValidator(issuer)
        );

        decoder.setJwtValidator(validator);
        return decoder;
    }

    @Bean
    Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> authorities = jwt.getClaimAsStringList("authorities");
            if (authorities == null) {
                authorities = List.of();
            }
            return Flux.fromIterable(
                    authorities.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList()
            );
        });
        return converter;
    }
}
