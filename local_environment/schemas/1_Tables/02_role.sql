create table vota_mas.role
(
    role_id    uuid         not null,
    created_at timestamp(6),
    updated_at timestamp(6),
    is_active  boolean      not null,
    name       varchar(100) not null
);

create table vota_mas.user_role
(
    user_id uuid not null,
    role_id uuid not null
);