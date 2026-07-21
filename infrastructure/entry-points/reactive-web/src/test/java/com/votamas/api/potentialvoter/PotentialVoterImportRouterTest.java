package com.votamas.api.potentialvoter;

import com.votamas.api.common.observability.HttpRequestLoggingFilter;
import com.votamas.api.common.validation.RequestValidator;
import com.votamas.api.common.web.PotentialVoterImportRequestExtractor;
import com.votamas.api.common.web.AuthenticatedUserIdResolver;
import com.votamas.api.config.ApiProperties;
import com.votamas.api.exceptions.GlobalExceptionHandler;
import com.votamas.api.potentialvoter.handlers.PotentialVoterHandler;
import com.votamas.api.potentialvoter.routers.PotentialVoterRouterRest;
import com.votamas.model.potentialvoter.PotentialVoterImportResult;
import com.votamas.usecase.potentialvoter.ImportPotentialVotersUseCase;
import com.votamas.usecase.potentialvoter.PotentialVoterUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PotentialVoterRouterRest.class, PotentialVoterHandler.class, ApiProperties.class})
@WebFluxTest
@Import({GlobalExceptionHandler.class, HttpRequestLoggingFilter.class,
        RequestValidator.class, PotentialVoterImportRequestExtractor.class})
class PotentialVoterImportRouterTest {
    @Autowired
    private WebTestClient client;

    @MockitoBean
    private PotentialVoterUseCase potentialVoterUseCase;

    @MockitoBean
    private ImportPotentialVotersUseCase importUseCase;

    @MockitoBean
    private AuthenticatedUserIdResolver authenticatedUserIdResolver;

    @Test
    void shouldImportValidMultipartRequest() {
        UUID leaderId = UUID.randomUUID();
        when(authenticatedUserIdResolver.resolve(any())).thenReturn(Mono.just(leaderId));
        when(importUseCase.execute(any(byte[].class), eq(leaderId)))
                .thenReturn(Mono.just(new PotentialVoterImportResult(1, 1, 0, 0, List.of())));

        client.post().uri("/api/v1/potential-voter/import")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(
                        parts(file("voters.xlsx", new byte[]{1})).build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalRows").isEqualTo(1)
                .jsonPath("$.successfulRows").isEqualTo(1)
                .jsonPath("$.failedRows").isEqualTo(0);
    }

    @Test
    void shouldRejectMissingFile() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        assertInvalid(builder, "file");
    }

    @Test
    void shouldRejectEmptyOrWrongExtensionFile() {
        assertInvalid(parts(file("voters.xlsx", new byte[0])), "file");
        assertInvalid(parts(file("voters.xls", new byte[]{1})), "file");
    }

    private void assertInvalid(MultipartBodyBuilder builder, String field) {
        client.post().uri("/api/v1/potential-voter/import")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BP400")
                .jsonPath("$.errors[?(@.field == '%s')]".formatted(field)).exists();
        verifyNoInteractions(importUseCase);
    }

    private MultipartBodyBuilder parts(ByteArrayResource file) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file);
        return builder;
    }

    private ByteArrayResource file(String filename, byte[] content) {
        return new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }
}
