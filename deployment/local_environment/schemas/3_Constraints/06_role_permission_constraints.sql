ALTER TABLE vota_mas.role_permission
    ADD CONSTRAINT pk_role_permission
        PRIMARY KEY (role_id, permission_id);

ALTER TABLE vota_mas.role_permission
    ADD CONSTRAINT fk_role_permission_role
        FOREIGN KEY (role_id)
            REFERENCES vota_mas.role(role_id);

ALTER TABLE vota_mas.role_permission
    ADD CONSTRAINT fk_role_permission_permission
        FOREIGN KEY (permission_id)
            REFERENCES vota_mas.permission(permission_id);
