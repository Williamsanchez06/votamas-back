INSERT INTO vota_mas.role (role_id, created_at, updated_at, is_active, name)
VALUES
('e50411ea-ab77-453c-a75a-3b085f9eaef2', now(), now(), true, 'ADMINISTRADOR'),
('6f4b927d-3c5a-4e51-8ad8-ff3d3029a001', now(), now(), true, 'LIDER');

INSERT INTO vota_mas.user_role (user_id, role_id)
VALUES ('121e1766-2d53-4f3d-a1e1-8b72f3285e16', 'e50411ea-ab77-453c-a75a-3b085f9eaef2');
