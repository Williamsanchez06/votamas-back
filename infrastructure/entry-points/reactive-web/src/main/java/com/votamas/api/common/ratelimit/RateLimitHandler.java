package com.votamas.api.common.ratelimit;

import com.votamas.api.config.ApiProperties;
import com.votamas.api.exceptions.SecurityExceptionHandler;
import com.votamas.api.common.ratelimit.ClientRateLimiterRegistry.RateLimitDecision;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

@Component
public class RateLimitHandler {
    private static final String LIMIT_HEADER = "X-RateLimit-Limit";
    private static final String REMAINING_HEADER = "X-RateLimit-Remaining";
    private static final String DEFAULT_POLICY = "default";
    private static final String LOGIN_POLICY = "login";
    private static final String IMPORT_POLICY = "import";
    private final RateLimitProperties properties;
    private final ClientRateLimiterRegistry rateLimiterRegistry;
    private final SecurityExceptionHandler exceptionHandler;
    private final String apiPath;
    private final String loginPath;
    private final String importPath;

    public RateLimitHandler(RateLimitProperties properties,
                            ClientRateLimiterRegistry rateLimiterRegistry,
                            SecurityExceptionHandler exceptionHandler,
                            ApiProperties apiProperties) {
        this.properties = properties;
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.exceptionHandler = exceptionHandler;
        this.apiPath = apiProperties.baseApiPath.concat("/");
        this.loginPath = apiProperties.baseApiPath.concat("/auth/login");
        this.importPath = apiProperties.baseApiPath.concat("/potential-voter/import");
    }

    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        if (!properties.enabled() || HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())
                || !path.startsWith(apiPath)) {
            return chain.filter(exchange);
        }

        PolicySelection selection = selectPolicy(path, exchange.getRequest().getMethod());
        return authenticatedClientId(exchange)
                .defaultIfEmpty(remoteClientId(exchange))
                .flatMap(clientId -> applyLimit(exchange, chain, clientId, selection));
    }

    private Mono<Void> applyLimit(ServerWebExchange exchange, WebFilterChain chain, String clientId,
                                  PolicySelection selection) {
        RateLimitDecision decision = rateLimiterRegistry.tryAcquire(
                clientId, selection.name(), selection.policy());
        if (!decision.permitted()) {
            return exceptionHandler.handleRateLimit(
                    exchange, decision.retryAfterSeconds(), selection.name());
        }

        exchange.getResponse().getHeaders().set(LIMIT_HEADER,
                Integer.toString(selection.policy().limitForPeriod()));
        exchange.getResponse().getHeaders().set(REMAINING_HEADER,
                Integer.toString(decision.remainingPermissions()));
        return chain.filter(exchange);
    }

    private PolicySelection selectPolicy(String path, HttpMethod method) {
        if (HttpMethod.POST.equals(method) && loginPath.equals(path)) {
            return new PolicySelection(LOGIN_POLICY, properties.loginPolicy());
        }
        if (HttpMethod.POST.equals(method) && importPath.equals(path)) {
            return new PolicySelection(IMPORT_POLICY, properties.importPolicy());
        }
        return new PolicySelection(DEFAULT_POLICY, properties.defaultPolicy());
    }

    private Mono<String> authenticatedClientId(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .ofType(JwtAuthenticationToken.class)
                .map(authentication -> authentication.getToken().getClaimAsString("userId"))
                .filter(StringUtils::hasText)
                .map(userId -> "user:" + userId);
    }

    private String remoteClientId(ServerWebExchange exchange) {
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        if (remoteAddress == null) {
            return "ip:unknown";
        }
        if (remoteAddress.getAddress() != null) {
            return "ip:" + remoteAddress.getAddress().getHostAddress();
        }
        return "ip:" + remoteAddress.getHostString();
    }

    private record PolicySelection(String name, RateLimitProperties.Policy policy) {
    }
}
