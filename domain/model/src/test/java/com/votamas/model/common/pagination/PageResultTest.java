package com.votamas.model.common.pagination;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import com.votamas.model.potentialvoter.PotentialVoterDetails;
import com.votamas.model.potentialvoter.VotingTable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageResultTest {

    @Test
    void shouldBuildFirstPageUsingZeroBasedIndex() {
        PageResult<Integer> result = PageResult.of(List.of(1, 2, 3), new PageQuery(0, 10), 23);

        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(23, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(List.of(1, 2, 3), result.content());
    }

    @Test
    void shouldCalculateIncompleteLastPage() {
        PageResult<Integer> result = PageResult.of(List.of(21, 22, 23), new PageQuery(2, 10), 23);

        assertEquals(2, result.page());
        assertEquals(3, result.content().size());
        assertEquals(3, result.totalPages());
    }

    @Test
    void shouldReturnZeroPagesWhenThereAreNoResults() {
        PageResult<Integer> result = PageResult.of(List.of(), new PageQuery(0, 10), 0);

        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
        assertEquals(List.of(), result.content());
    }

    @Test
    void shouldReturnOnePageWhenTotalIsLessThanSize() {
        PageResult<Integer> result = PageResult.of(List.of(1, 2), new PageQuery(0, 10), 2);

        assertEquals(1, result.totalPages());
    }

    @Test
    void shouldCalculateOffsetForPageZeroAndPageOne() {
        assertEquals(0, new PageQuery(0, 10).offset());
        assertEquals(10, new PageQuery(1, 10).offset());
    }

    @Test
    void shouldRejectInvalidPaginationValues() {
        assertThrows(IllegalArgumentException.class, () -> new PageQuery(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> new PageQuery(0, 0));
        assertThrows(IllegalArgumentException.class, () -> new PageQuery(0, 101));
    }

    @Test
    void shouldPreservePaginationWhenContentIsEnriched() {
        PotentialVoterDetails voter = PotentialVoterDetails.builder()
                .id(UUID.randomUUID())
                .votingTable(new VotingTable(UUID.randomUUID(), null, 10))
                .build();
        PageResult<PotentialVoterDetails> result = PageResult.of(List.of(voter), new PageQuery(1, 10), 12);

        assertEquals(1, result.page());
        assertEquals(10, result.size());
        assertEquals(12, result.totalElements());
        assertEquals(2, result.totalPages());
        assertEquals(10, result.content().getFirst().votingTable().tableNumber());
    }
}
