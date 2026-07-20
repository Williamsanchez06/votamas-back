package com.votamas.api.common.web;

import com.votamas.api.common.validation.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaginationRequestParserTest {
    @Test
    void shouldUseDefaultsWhenParametersAreAbsent() {
        ServerRequest request = requestWith(Optional.empty(), Optional.empty());
        var result = PaginationRequestParser.from(request);
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(20);
    }

    @Test
    void shouldAcceptValidParameters() {
        var result = PaginationRequestParser.from(requestWith(Optional.of("1"), Optional.of("100")));
        assertThat(result.page()).isOne();
        assertThat(result.size()).isEqualTo(100);
    }

    @Test
    void shouldRejectNegativePage() {
        assertInvalid(Optional.of("-1"), Optional.of("10"));
    }

    @Test
    void shouldRejectZeroNegativeAndExcessiveSize() {
        assertInvalid(Optional.of("0"), Optional.of("0"));
        assertInvalid(Optional.of("0"), Optional.of("-1"));
        assertInvalid(Optional.of("0"), Optional.of("101"));
    }

    @Test
    void shouldRejectNonNumericParameters() {
        assertInvalid(Optional.of("first"), Optional.of("many"));
    }

    private void assertInvalid(Optional<String> page, Optional<String> size) {
        assertThatThrownBy(() -> PaginationRequestParser.from(requestWith(page, size)))
                .isInstanceOf(InvalidRequestException.class);
    }

    private ServerRequest requestWith(Optional<String> page, Optional<String> size) {
        ServerRequest request = mock(ServerRequest.class);
        when(request.queryParam("page")).thenReturn(page);
        when(request.queryParam("size")).thenReturn(size);
        return request;
    }
}
