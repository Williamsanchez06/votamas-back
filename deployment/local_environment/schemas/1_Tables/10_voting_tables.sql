CREATE TABLE vota_mas.voting_tables
(
    voting_table_id  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    polling_place_id UUID         NOT NULL,
    table_number     INTEGER      NOT NULL,
    created_at       TIMESTAMP(6) NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP(6) NOT NULL DEFAULT now(),

    CONSTRAINT uq_voting_tables_place_number
        UNIQUE (polling_place_id, table_number),

    CONSTRAINT fk_voting_tables_polling_place
        FOREIGN KEY (polling_place_id)
        REFERENCES vota_mas.polling_places(polling_place_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_voting_tables_table_number_positive
        CHECK (table_number > 0)
);

CREATE INDEX idx_voting_tables_polling_place_id
    ON vota_mas.voting_tables(polling_place_id);
