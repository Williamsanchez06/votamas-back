package com.votamas.api.common.ratelimit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "rate-limit")
public record RateLimitProperties(
        boolean enabled,
        @Min(1) long maxBuckets,
        @NotNull Duration expireAfterAccess,
        @Valid @NotNull Policy defaultPolicy,
        @Valid @NotNull Policy loginPolicy,
        @Valid @NotNull Policy importPolicy
) {
    @AssertTrue(message = "rate-limit.expire-after-access debe ser mayor que cero")
    public boolean isExpireAfterAccessValid() {
        return expireAfterAccess == null || (!expireAfterAccess.isZero() && !expireAfterAccess.isNegative());
    }

    public record Policy(
            @Min(1) int limitForPeriod,
            @NotNull Duration limitRefreshPeriod
    ) {
        @AssertTrue(message = "limit-refresh-period debe ser mayor que cero")
        public boolean isLimitRefreshPeriodValid() {
            return limitRefreshPeriod == null
                    || (!limitRefreshPeriod.isZero() && !limitRefreshPeriod.isNegative());
        }
    }
}
