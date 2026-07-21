CREATE TABLE vota_mas.potential_voters
(
    potential_voter_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    identification     VARCHAR(30)  NOT NULL UNIQUE,
    first_name         VARCHAR(150) NOT NULL,
    last_name          VARCHAR(150) NOT NULL,
    voting_table_id    UUID         NOT NULL,
    registration_date  DATE         NOT NULL DEFAULT CURRENT_DATE,
    assigned_leader_id UUID         NOT NULL,
    created_at         TIMESTAMP(6) NOT NULL DEFAULT now(),
    updated_at         TIMESTAMP(6) NOT NULL DEFAULT now(),

    CONSTRAINT fk_potential_voters_voting_table
        FOREIGN KEY (voting_table_id)
        REFERENCES vota_mas.voting_tables(voting_table_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_potential_voters_assigned_leader
        FOREIGN KEY (assigned_leader_id)
        REFERENCES vota_mas.users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE INDEX idx_potential_voters_voting_table_id
    ON vota_mas.potential_voters(voting_table_id);

CREATE INDEX idx_potential_voters_assigned_leader_id
    ON vota_mas.potential_voters(assigned_leader_id);
