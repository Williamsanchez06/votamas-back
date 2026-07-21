CREATE TABLE vota_mas.permission
(
    permission_id UUID PRIMARY KEY,
    created_at    TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT now(),
    name          VARCHAR(255) NOT NULL,
    title         VARCHAR(255) NOT NULL,
    module_id     UUID         NOT NULL,
    CONSTRAINT uq_permission_name UNIQUE (name),
    CONSTRAINT fk_permission_module FOREIGN KEY (module_id)
        REFERENCES vota_mas.module(module_id)
);
