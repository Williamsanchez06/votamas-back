CREATE TABLE vota_mas.module
(
    module_order   INTEGER      NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT now(),
    module_id      UUID PRIMARY KEY,
    parent_id      UUID NULL,
    name           VARCHAR(255) NOT NULL,
    route          VARCHAR(255) NOT NULL,
    description    TEXT,
    primary_icon   VARCHAR(255),
    secondary_icon VARCHAR(255),
    tertiary_icon  VARCHAR(255)
);
