CREATE TABLE vota_mas.role
(
    role_id    UUID         NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT now(),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT now(),
    is_active  BOOLEAN      NOT NULL,
    name       VARCHAR(100) NOT NULL
);

CREATE TABLE vota_mas.user_role
(
    user_id UUID NOT NULL,
    role_id UUID NOT NULL
);
