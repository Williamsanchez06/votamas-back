package com.votamas.api.common.web;

import com.votamas.api.common.validation.InvalidRequestException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathVariableParserTest {
    @Test
    void shouldParseValidUuid() {
        UUID id = UUID.randomUUID();
        assertThat(PathVariableParser.uuid(id.toString(), "id")).isEqualTo(id);
    }

    @Test
    void shouldRejectInvalidUuid() {
        assertThatThrownBy(() -> PathVariableParser.uuid("invalid", "id"))
                .isInstanceOf(InvalidRequestException.class);
    }
}
