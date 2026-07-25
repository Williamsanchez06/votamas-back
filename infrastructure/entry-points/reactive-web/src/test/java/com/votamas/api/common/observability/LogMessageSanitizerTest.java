package com.votamas.api.common.observability;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogMessageSanitizerTest {

    @Test
    void shouldReplaceLineBreaksAndHandleMissingMessages() {
        assertThat(LogMessageSanitizer.sanitize("first\r\nsecond"))
                .isEqualTo("first  second");
        assertThat(LogMessageSanitizer.sanitize(null)).isEqualTo("-");
    }
}
