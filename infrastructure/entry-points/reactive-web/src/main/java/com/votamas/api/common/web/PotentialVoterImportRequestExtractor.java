package com.votamas.api.common.web;

import com.votamas.api.common.validation.FieldValidationError;
import com.votamas.api.common.validation.InvalidRequestException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class PotentialVoterImportRequestExtractor {
    private static final MediaType XLSX_MEDIA_TYPE = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    private final int maxFileSizeBytes;

    public PotentialVoterImportRequestExtractor(
            @Value("${imports.potential-voters.max-file-size-bytes:10485760}") int maxFileSizeBytes) {
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    public Mono<PotentialVoterImportRequest> extract(ServerRequest request) {
        return request.multipartData().flatMap(parts -> {
            List<FieldValidationError> errors = new ArrayList<>();
            Part filePart = parts.getFirst("file");
            FilePart file = filePart instanceof FilePart value ? value : null;
            if (file == null) {
                errors.add(new FieldValidationError("file", "El archivo es obligatorio"));
            } else if (!file.filename().toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
                errors.add(new FieldValidationError("file", "El archivo debe tener extensión .xlsx"));
            } else if (!isSupportedContentType(file.headers().getContentType())) {
                errors.add(new FieldValidationError("file", "El tipo de contenido del archivo no es válido"));
            }

            if (!errors.isEmpty()) {
                return Mono.error(new InvalidRequestException(errors));
            }

            return DataBufferUtils.join(file.content(), maxFileSizeBytes)
                    .map(buffer -> {
                        byte[] content = new byte[buffer.readableByteCount()];
                        try {
                            buffer.read(content);
                        } finally {
                            DataBufferUtils.release(buffer);
                        }
                        if (content.length == 0) {
                            throw new InvalidRequestException(List.of(
                                    new FieldValidationError("file", "El archivo no puede estar vacío")));
                        }
                        return new PotentialVoterImportRequest(content);
                    })
                    .onErrorMap(DataBufferLimitException.class, exception ->
                            new InvalidRequestException(List.of(new FieldValidationError(
                                    "file", "El archivo supera el tamaño máximo permitido"))))
                    .switchIfEmpty(Mono.error(new InvalidRequestException(List.of(
                            new FieldValidationError("file", "El archivo no puede estar vacío")))));
        });
    }

    private boolean isSupportedContentType(MediaType contentType) {
        return contentType == null
                || XLSX_MEDIA_TYPE.isCompatibleWith(contentType)
                || MediaType.APPLICATION_OCTET_STREAM.isCompatibleWith(contentType);
    }

}
