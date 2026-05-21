\set ON_ERROR_STOP on

\echo '=========================================='
\echo 'RECREANDO BASE DE DATOS votamas'
\echo '=========================================='

DROP DATABASE IF EXISTS votamas WITH (FORCE);
CREATE DATABASE votamas;

\connect votamas

\echo '=========================================='
\echo 'CREANDO EXTENSIONES Y SCHEMAS'
\echo '=========================================='

CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE SCHEMA IF NOT EXISTS vota_mas;

\echo '=========================================='
\echo 'CREANDO TABLAS'
\echo '=========================================='

\i schemas/1_Tables/01_users.sql
\i schemas/1_Tables/02_role.sql
\i schemas/1_Tables/03_module.sql
\i schemas/1_Tables/04_type.sql
\i schemas/1_Tables/05_module_type.sql
\i schemas/1_Tables/06_permission.sql

\echo '=========================================='
\echo 'CREANDO CONSTRAINTS'
\echo '=========================================='

\i schemas/3_Constraints/02_role_constraints.sql
\i schemas/3_Constraints/03_module_constraint.sql
\i schemas/3_Constraints/04_module_type_constraints.sql
\i schemas/3_Constraints/05_permission_constraints.sql

\echo '=========================================='
\echo 'INSERTANDO DATOS'
\echo '=========================================='

\i inserts/users.sql
\i inserts/role.sql
\i inserts/module.sql
\i inserts/type.sql
\i inserts/module_type.sql
\i inserts/permission.sql

\echo '=========================================='
\echo 'BASE DE DATOS FINALIZADA CORRECTAMENTE'
\echo '=========================================='
