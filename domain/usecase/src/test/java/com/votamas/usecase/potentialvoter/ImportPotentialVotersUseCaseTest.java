package com.votamas.usecase.potentialvoter;

import com.votamas.model.exception.NotFoundException;
import com.votamas.model.potentialvoter.PotentialVoter;
import com.votamas.model.potentialvoter.PotentialVoterImportData;
import com.votamas.model.potentialvoter.PotentialVoterImportRow;
import com.votamas.model.potentialvoter.VotingTableReference;
import com.votamas.model.potentialvoter.gateways.PotentialVoterRepository;
import com.votamas.model.potentialvoter.gateways.PotentialVoterSpreadsheetReader;
import com.votamas.model.potentialvoter.gateways.VotingLocationRepository;
import com.votamas.model.user.User;
import com.votamas.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ImportPotentialVotersUseCaseTest {
    private final PotentialVoterSpreadsheetReader reader = mock(PotentialVoterSpreadsheetReader.class);
    private final PotentialVoterRepository voterRepository = mock(PotentialVoterRepository.class);
    private final VotingLocationRepository locationRepository = mock(VotingLocationRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UUID leaderId = UUID.randomUUID();
    private final UUID tableId = UUID.randomUUID();
    private ImportPotentialVotersUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ImportPotentialVotersUseCase(reader, voterRepository, locationRepository, userRepository);
        when(userRepository.findById(leaderId))
                .thenReturn(Mono.just(User.builder().id(leaderId).active(true).build()));
        when(voterRepository.findExistingIdentifications(any())).thenReturn(Flux.empty());
        when(locationRepository.findAllVotingTableReferences()).thenReturn(Flux.empty());
    }

    @Test
    void shouldImportValidRowsAndResolveRepeatedTableOnce() {
        when(reader.read(any())).thenReturn(Mono.just(new PotentialVoterImportData(List.of(
                row(2, "100", "Comuna 1", "Colegio", "12"),
                row(3, "101", " comuna 1 ", " colegio ", "12")), 1)));
        when(locationRepository.findAllVotingTableReferences()).thenReturn(Flux.just(
                new VotingTableReference(tableId, "Comuna 1", "Colegio", 12)));
        when(voterRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.execute(new byte[]{1}, leaderId))
                .assertNext(result -> {
                    assertThat(result.totalRows()).isEqualTo(2);
                    assertThat(result.successfulRows()).isEqualTo(2);
                    assertThat(result.failedRows()).isZero();
                    assertThat(result.skippedRows()).isOne();
                })
                .verifyComplete();

        verify(locationRepository, times(1)).findAllVotingTableReferences();
        verify(voterRepository, times(2)).save(any());
    }

    @Test
    void shouldReportMissingTableWithoutSaving() {
        when(reader.read(any())).thenReturn(Mono.just(new PotentialVoterImportData(
                List.of(row(8, "100", "Comuna 1", "Colegio", "12")), 0)));

        StepVerifier.create(useCase.execute(new byte[]{1}, leaderId))
                .assertNext(result -> {
                    assertThat(result.failedRows()).isOne();
                    assertThat(result.errors().getFirst().row()).isEqualTo(8);
                    assertThat(result.errors().getFirst().message()).contains("No se encontró la mesa 12");
                })
                .verifyComplete();
        verify(voterRepository, never()).save(any());
    }

    @Test
    void shouldStopWhenLeaderDoesNotExist() {
        UUID missingLeader = UUID.randomUUID();
        when(userRepository.findById(missingLeader)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(new byte[]{1}, missingLeader))
                .expectError(NotFoundException.class)
                .verify();
        verifyNoInteractions(reader, voterRepository, locationRepository);
    }

    @Test
    void shouldReportExistingAndFileDuplicateIdentifications() {
        when(reader.read(any())).thenReturn(Mono.just(new PotentialVoterImportData(List.of(
                row(2, "100", "Comuna 1", "Colegio", "12"),
                row(3, "100", "Comuna 1", "Colegio", "12")), 0)));
        when(voterRepository.findExistingIdentifications(any())).thenReturn(Flux.just("100"));

        StepVerifier.create(useCase.execute(new byte[]{1}, leaderId))
                .assertNext(result -> {
                    assertThat(result.failedRows()).isEqualTo(2);
                    assertThat(result.errors()).extracting(error -> error.message())
                            .containsExactly("La identificación ya se encuentra registrada",
                                    "La identificación está duplicada dentro del archivo");
                })
                .verifyComplete();
        verify(voterRepository, times(1)).findExistingIdentifications(any());
    }

    @Test
    void shouldContinueWithValidRowsWhenOtherRowsAreInvalid() {
        when(reader.read(any())).thenReturn(Mono.just(new PotentialVoterImportData(List.of(
                row(2, "", "", "", "0"),
                row(3, "101", "Comuna 1", "Colegio", "12")), 0)));
        when(locationRepository.findAllVotingTableReferences()).thenReturn(Flux.just(
                new VotingTableReference(tableId, "Comuna 1", "Colegio", 12)));
        when(voterRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.execute(new byte[]{1}, leaderId))
                .assertNext(result -> {
                    assertThat(result.successfulRows()).isOne();
                    assertThat(result.failedRows()).isOne();
                    assertThat(result.errors().getFirst().row()).isEqualTo(2);
                })
                .verifyComplete();

        ArgumentCaptor<PotentialVoter> captor = ArgumentCaptor.forClass(PotentialVoter.class);
        verify(voterRepository).save(captor.capture());
        assertThat(captor.getValue().identification()).isEqualTo("101");
    }

    private PotentialVoterImportRow row(int number, String identification, String zone,
                                        String place, String table) {
        return new PotentialVoterImportRow(number, identification, "Ana", "Pérez",
                zone, place, table);
    }
}
