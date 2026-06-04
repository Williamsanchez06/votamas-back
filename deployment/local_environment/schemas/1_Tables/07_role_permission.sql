CREATE TABLE vota_mas.role_permission
(
    role_id       UUID NOT NULL,
    permission_id UUID NOT NULL,
    CONSTRAINT pk_role_permission PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id)
        REFERENCES vota_mas.role(role_id),
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id)
        REFERENCES vota_mas.permission(permission_id)
);
