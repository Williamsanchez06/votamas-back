package com.votamas.api.common.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ClientRateLimiterRegistry {
    private final Cache<RateLimiterKey, RateLimiter> rateLimiters;

    public ClientRateLimiterRegistry(RateLimitProperties properties) {
        rateLimiters = Caffeine.newBuilder()
                .maximumSize(properties.maxBuckets())
                .expireAfterAccess(properties.expireAfterAccess())
                .build();
    }

    public RateLimitDecision tryAcquire(String clientId, String policyName,
                                        RateLimitProperties.Policy policy) {
        RateLimiterKey key = new RateLimiterKey(clientId, policyName);
        RateLimiter rateLimiter = rateLimiters.get(key, ignored -> createRateLimiter(key, policy));
        boolean permitted = rateLimiter.acquirePermission();
        int remainingPermissions = Math.max(0, rateLimiter.getMetrics().getAvailablePermissions());
        return new RateLimitDecision(permitted, remainingPermissions,
                retryAfterSeconds(policy.limitRefreshPeriod()));
    }

    private RateLimiter createRateLimiter(RateLimiterKey key, RateLimitProperties.Policy policy) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(policy.limitForPeriod())
                .limitRefreshPeriod(policy.limitRefreshPeriod())
                .timeoutDuration(Duration.ZERO)
                .build();
        return RateLimiter.of(key.name(), config);
    }

    private long retryAfterSeconds(Duration refreshPeriod) {
        long seconds = refreshPeriod.getSeconds();
        if (refreshPeriod.getNano() > 0 && seconds < Long.MAX_VALUE) {
            seconds++;
        }
        return Math.max(1, seconds);
    }

    public record RateLimitDecision(boolean permitted, int remainingPermissions, long retryAfterSeconds) {
    }

    private record RateLimiterKey(String clientId, String policyName) {
        private String name() {
            return policyName + ":" + Integer.toUnsignedString(hashCode(), 16);
        }
    }
}
