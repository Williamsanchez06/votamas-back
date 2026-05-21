ALTER TABLE vota_mas.module
    ADD CONSTRAINT fk_module_parent
        FOREIGN KEY (parent_id)
            REFERENCES vota_mas.module(module_id);
