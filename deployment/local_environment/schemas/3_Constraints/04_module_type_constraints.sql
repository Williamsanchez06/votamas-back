ALTER TABLE vota_mas.module_type
    ADD CONSTRAINT pk_module_type
        PRIMARY KEY (module_id, type_id);

ALTER TABLE vota_mas.module_type
    ADD CONSTRAINT fk_module_type_module
        FOREIGN KEY (module_id)
            REFERENCES vota_mas.module(module_id);

ALTER TABLE vota_mas.module_type
    ADD CONSTRAINT fk_module_type_type
        FOREIGN KEY (type_id)
            REFERENCES vota_mas.type(type_id);
