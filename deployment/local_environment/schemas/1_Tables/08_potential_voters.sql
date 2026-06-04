CREATE TABLE vota_mas.potential_voters
(
    potential_voter_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    identification     VARCHAR(30)  NOT NULL UNIQUE,
    first_name         VARCHAR(150) NOT NULL,
    last_name          VARCHAR(150) NOT NULL,
    commune            VARCHAR(100),
    neighborhood       VARCHAR(150),
    polling_place      VARCHAR(200),
    polling_station    VARCHAR(50),
    registration_date  DATE         NOT NULL DEFAULT CURRENT_DATE,
    assigned_leader_id UUID         NOT NULL,
    created_at         TIMESTAMP(6) NOT NULL DEFAULT now(),
    updated_at         TIMESTAMP(6) NOT NULL DEFAULT now(),
    CONSTRAINT fk_potential_voters_assigned_leader FOREIGN KEY (assigned_leader_id)
        REFERENCES vota_mas.users(user_id)
);
