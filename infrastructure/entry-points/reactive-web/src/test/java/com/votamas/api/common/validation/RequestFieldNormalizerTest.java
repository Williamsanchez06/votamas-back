package com.votamas.api.common.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestFieldNormalizerTest {

    @Test
    void shouldNormalizeTextAndEmailWithoutChangingNullValues() {
        assertThat(RequestFieldNormalizer.normalizeText("  Ana   María  ")).isEqualTo("Ana María");
        assertThat(RequestFieldNormalizer.normalizeEmail(" ANA@EXAMPLE.COM "))
                .isEqualTo("ana@example.com");
        assertThat(RequestFieldNormalizer.normalizeText(null)).isNull();
        assertThat(RequestFieldNormalizer.normalizeEmail(null)).isNull();
    }
}
