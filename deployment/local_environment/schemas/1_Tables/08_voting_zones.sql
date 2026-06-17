CREATE TABLE vota_mas.voting_zones
(
    voting_zone_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name           VARCHAR(100) NOT NULL UNIQUE,
    created_at     TIMESTAMP(6) NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP(6) NOT NULL DEFAULT now()
);
