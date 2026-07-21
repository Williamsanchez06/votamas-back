package com.votamas.r2dbc.potentialvoter.entities;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDate;
import java.util.UUID;

@Builder(toBuilder = true)
@Table(name = "potential_voters")
public record PotentialVoterData(
        @Id @Column("potential_voter_id") UUID id,
        @Column("identification") String identification,
        @Column("first_name") String firstName,
        @Column("last_name") String lastName,
        @Column("voting_table_id") UUID votingTableId,
        @Column("registration_date") LocalDate registrationDate,
        @Column("assigned_leader_id") UUID assignedLeaderId
) {}
