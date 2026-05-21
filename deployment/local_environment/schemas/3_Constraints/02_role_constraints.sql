ALTER TABLE vota_mas.role
    ADD CONSTRAINT pk_role
        PRIMARY KEY (role_id);

ALTER TABLE vota_mas.user_role
    ADD CONSTRAINT pk_user_role
        PRIMARY KEY (user_id, role_id);

ALTER TABLE vota_mas.user_role
    ADD CONSTRAINT fk_user_role_user
        FOREIGN KEY (user_id)
            REFERENCES vota_mas.users(user_id);

ALTER TABLE vota_mas.user_role
    ADD CONSTRAINT fk_user_role_role
        FOREIGN KEY (role_id)
            REFERENCES vota_mas.role(role_id);
