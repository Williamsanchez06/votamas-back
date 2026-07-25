package com.votamas.usecase.potentialvoter;

import com.votamas.model.exception.BusinessException;
import com.votamas.model.exception.MessageError;
import com.votamas.model.potentialvoter.PotentialVoterSearchCriteria;
import com.votamas.model.potentialvoter.gateways.PotentialVoterQueryRepository;
import com.votamas.model.potentialvoter.gateways.PotentialVoterSpreadsheetWriter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class ExportPotentialVotersUseCase {
    private final PotentialVoterQueryRepository potentialVoterQueryRepository;
    private final PotentialVoterSpreadsheetWriter spreadsheetWriter;
    private final PotentialVoterAccessUseCase potentialVoterAccessUseCase;

    public Mono<byte[]> execute(
            PotentialVoterSearchCriteria criteria, int maxRows, UUID authenticatedUserId) {
        return potentialVoterAccessUseCase.scope(criteria, authenticatedUserId)
                .flatMapMany(scopedCriteria ->
                        potentialVoterQueryRepository.findForExport(scopedCriteria, maxRows + 1))
                .collectList()
                .flatMap(rows -> {
                    if (rows.size() > maxRows) {
                        return Mono.error(new BusinessException(
                                MessageError.SPREADSHEET_EXPORT_LIMIT_EXCEEDED));
                    }
                    return spreadsheetWriter.write(rows);
                });
    }
}
