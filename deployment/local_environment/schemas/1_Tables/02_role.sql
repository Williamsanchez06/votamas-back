CREATE TABLE vota_mas.role
(
    role_id    UUID PRIMARY KEY,
    created_at TIMESTAMP(6) NOT NULL DEFAULT now(),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT now(),
    is_active  BOOLEAN      NOT NULL,
    name       VARCHAR(100) NOT NULL
);

CREATE TABLE vota_mas.user_role
(
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    CONSTRAINT pk_user_role PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id)
        REFERENCES vota_mas.users(user_id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id)
        REFERENCES vota_mas.role(role_id)
);
