package com.votamas.api.potentialvoter;

import com.votamas.api.common.validation.RequestValidator;
import com.votamas.api.common.web.AuthenticatedUserIdResolver;
import com.votamas.api.common.web.PotentialVoterImportRequestExtractor;
import com.votamas.api.potentialvoter.dtos.PotentialVoterRequestDTO;
import com.votamas.api.potentialvoter.handlers.PotentialVoterHandler;
import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.usecase.potentialvoter.ImportPotentialVotersUseCase;
import com.votamas.usecase.potentialvoter.PotentialVoterUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PotentialVoterHandlerTest {
    @Test
    void shouldAssignAuthenticatedUserWhenCreatingPotentialVoter() {
        PotentialVoterUseCase voterUseCase = mock(PotentialVoterUseCase.class);
        RequestValidator validator = mock(RequestValidator.class);
        AuthenticatedUserIdResolver userIdResolver = mock(AuthenticatedUserIdResolver.class);
        ServerRequest request = mock(ServerRequest.class);
        UUID tableId = UUID.randomUUID();
        UUID authenticatedUserId = UUID.randomUUID();
        var requestDto = new PotentialVoterRequestDTO("123", "Ana", "Pérez", tableId);

        when(validator.body(request, PotentialVoterRequestDTO.class)).thenReturn(Mono.just(requestDto));
        when(userIdResolver.resolve(request)).thenReturn(Mono.just(authenticatedUserId));
        when(voterUseCase.savePotentialVoter(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        var handler = new PotentialVoterHandler(voterUseCase, validator,
                mock(PotentialVoterImportRequestExtractor.class),
                mock(ImportPotentialVotersUseCase.class), userIdResolver);

        StepVerifier.create(handler.createPotentialVoter(request))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();

        ArgumentCaptor<PotentialVoter> captor = ArgumentCaptor.forClass(PotentialVoter.class);
        verify(voterUseCase).savePotentialVoter(captor.capture());
        assertThat(captor.getValue().assignedLeaderId()).isEqualTo(authenticatedUserId);
        assertThat(captor.getValue().votingTableId()).isEqualTo(tableId);
    }
}
