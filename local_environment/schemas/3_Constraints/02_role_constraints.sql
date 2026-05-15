alter table vota_mas.role
    add constraint pk_role
        primary key (role_id);

alter table vota_mas.user_role
    add constraint pk_user_role
        primary key (user_id, role_id);

alter table vota_mas.user_role
    add constraint fk_user_role_user
        foreign key (user_id)
            references vota_mas."user"(user_id);

alter table vota_mas.user_role
    add constraint fk_user_role_role
        foreign key (role_id)
            references vota_mas.role(role_id);