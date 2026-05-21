CREATE TABLE vota_mas.permission
(
    permission_id UUID PRIMARY KEY,
    created_at    TIMESTAMP    NOT NULL DEFAULT now(),
    name          VARCHAR(255) NOT NULL,
    title         VARCHAR(255) NOT NULL,
    updated_at    TIMESTAMP    NOT NULL DEFAULT now(),
    module_id     UUID         NOT NULL
);
