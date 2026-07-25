package com.votamas.api.potentialvoter.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "exports.potential-voters")
public record PotentialVoterExportProperties(@Min(1) int maxRows) {
}
