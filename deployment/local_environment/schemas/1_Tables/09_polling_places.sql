CREATE TABLE vota_mas.polling_places
(
    polling_place_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    voting_zone_id   UUID         NOT NULL,
    name             VARCHAR(255) NOT NULL,
    created_at       TIMESTAMP(6) NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP(6) NOT NULL DEFAULT now(),

    CONSTRAINT uq_polling_places_zone_name
        UNIQUE (voting_zone_id, name),

    CONSTRAINT fk_polling_places_voting_zone
        FOREIGN KEY (voting_zone_id)
        REFERENCES vota_mas.voting_zones(voting_zone_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE INDEX idx_polling_places_voting_zone_id
    ON vota_mas.polling_places(voting_zone_id);
