package com.votamas.api.potentialvoter.search;

import com.votamas.api.common.validation.FieldValidationError;
import com.votamas.api.common.validation.InvalidRequestException;
import com.votamas.api.common.web.PaginationRequestParser;
import com.votamas.api.common.web.PathVariableParser;
import com.votamas.model.potentialvoter.PotentialVoterSearchCriteria;
import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class PotentialVoterSearchCriteriaParser {
    private static final int MAX_IDENTIFICATION_LENGTH = 30;

    public static PotentialVoterSearchCriteria from(ServerRequest request) {
        String identification = normalizedIdentification(request);
        return new PotentialVoterSearchCriteria(
                PaginationRequestParser.from(request),
                identification,
                optionalUuid(request, "pollingPlaceId"),
                optionalUuid(request, "votingZoneId"),
                optionalUuid(request, "assignedLeaderId")
        );
    }

    private String normalizedIdentification(ServerRequest request) {
        String identification = request.queryParam("identification")
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElse(null);
        if (identification != null && identification.length() > MAX_IDENTIFICATION_LENGTH) {
            throw new InvalidRequestException(List.of(new FieldValidationError(
                    "identification", "La identificación no puede superar 30 caracteres")));
        }
        return identification;
    }

    private UUID optionalUuid(ServerRequest request, String name) {
        return request.queryParam(name)
                .map(String::trim)
                .map(value -> PathVariableParser.uuid(value, name))
                .orElse(null);
    }
}
