CREATE TABLE vota_mas.module_type
(
    module_id UUID NOT NULL,
    type_id   UUID NOT NULL,
    CONSTRAINT pk_module_type PRIMARY KEY (module_id, type_id),
    CONSTRAINT fk_module_type_module FOREIGN KEY (module_id)
        REFERENCES vota_mas.module(module_id),
    CONSTRAINT fk_module_type_type FOREIGN KEY (type_id)
        REFERENCES vota_mas.type(type_id)
);
