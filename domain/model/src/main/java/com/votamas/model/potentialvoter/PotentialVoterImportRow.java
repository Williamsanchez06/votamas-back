package com.votamas.model.potentialvoter;

public record PotentialVoterImportRow(
        int rowNumber,
        String identification,
        String firstName,
        String lastName,
        String votingZoneName,
        String pollingPlaceName,
        String tableNumber
) {
}
