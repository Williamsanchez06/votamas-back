ALTER TABLE vota_mas.permission
    ADD CONSTRAINT fk_permission_module
        FOREIGN KEY (module_id)
            REFERENCES vota_mas.module (module_id);