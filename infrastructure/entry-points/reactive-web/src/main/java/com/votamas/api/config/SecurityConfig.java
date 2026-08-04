package com.votamas.api.config;

import com.votamas.api.exceptions.SecurityExceptionHandler;
import com.votamas.api.common.ratelimit.RateLimitHandler;
import com.votamas.model.exception.MessageError;
import com.votamas.model.user.gateways.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                     SecurityExceptionHandler exceptionHandler,
                                                     CorsConfigurationSource corsConfigurationSource,
                                                     ApiProperties apiProperties,
                                                     RateLimitHandler rateLimitHandler,
                                                     Converter<Jwt, Mono<AbstractAuthenticationToken>>
                                                             jwtAuthenticationConverter) {
        String basePath = apiProperties.baseApiPath;
        String userPath = basePath.concat("/user/**");
        String userStatusPath = basePath.concat("/user/*/status");
        String potentialVoterPath = basePath.concat("/potential-voter/**");
        String votingZonePath = basePath.concat("/voting-zones/**");
        String activityPath = basePath.concat("/activity/**");

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .addFilterAfter(rateLimitHandler::filter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((exchange, exception) -> exceptionHandler.handle(
                                exchange, MessageError.AUTHENTICATION_REQUIRED, exception))
                        .accessDeniedHandler((exchange, exception) -> exceptionHandler.handle(
                                exchange, MessageError.ACCESS_DENIED, exception)))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(basePath.concat("/auth/login"), "/actuator/health").permitAll()
                        .pathMatchers(HttpMethod.GET, basePath.concat("/auth/me")).authenticated()
                        .pathMatchers("/actuator/prometheus").authenticated()
                        .pathMatchers(HttpMethod.GET, activityPath).hasAuthority("GET_USER")
                        .pathMatchers(HttpMethod.GET, userPath).hasAuthority("GET_USER")
                        .pathMatchers(HttpMethod.POST, userPath).hasAuthority("CREATE_USER")
                        .pathMatchers(HttpMethod.PUT, userPath).hasAuthority("EDIT_USER")
                        .pathMatchers(HttpMethod.PATCH, userStatusPath).hasAuthority("CHANGE_USER_STATUS")
                        .pathMatchers(HttpMethod.PATCH, userPath).hasAuthority("EDIT_USER")
                        .pathMatchers(HttpMethod.GET, potentialVoterPath).hasAuthority("GET_POTENTIAL_VOTER")
                        .pathMatchers(HttpMethod.GET, votingZonePath).hasAuthority("GET_POTENTIAL_VOTER")
                        .pathMatchers(HttpMethod.POST, potentialVoterPath).hasAuthority("CREATE_POTENTIAL_VOTER")
                        .pathMatchers(HttpMethod.PUT, potentialVoterPath).hasAuthority("EDIT_POTENTIAL_VOTER")
                        .pathMatchers(HttpMethod.DELETE, potentialVoterPath).hasAuthority("DELETE_POTENTIAL_VOTER")
                        .anyExchange().denyAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
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
    Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter(
            UserRepository userRepository) {
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
        return jwt -> authenticatedUserId(jwt)
                .flatMap(userRepository::isActiveById)
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(new BadCredentialsException("Usuario inactivo o inexistente")))
                .then(converter.convert(jwt));
    }

    private Mono<UUID> authenticatedUserId(Jwt jwt) {
        String userId = jwt.getClaimAsString("userId");
        try {
            return Mono.just(UUID.fromString(userId));
        } catch (IllegalArgumentException | NullPointerException exception) {
            return Mono.error(new BadCredentialsException("Token sin identificador de usuario válido"));
        }
    }
}
