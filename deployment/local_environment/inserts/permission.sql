INSERT INTO vota_mas.permission (permission_id, created_at, updated_at, name, title, module_id)
VALUES
('34698c89-3bd1-47a0-b225-6cd843bfb1a5', now(), now(), 'EDIT_USER', 'Editar usuario', 'b71340e8-b44b-477f-8475-af42c580668b'),
('eed952cc-f4df-4ba8-944d-284f79fa5001', now(), now(), 'GET_USER', 'Listar usuarios', 'b71340e8-b44b-477f-8475-af42c580668b'),
('ddf47c42-2d13-4979-a5ba-58af17103b3d', now(), now(), 'UPDATE_PASSWORD', 'Actualizar contraseña', 'b71340e8-b44b-477f-8475-af42c580668b'),
('697bb69f-2aa1-42af-98c6-885827ffb083', now(), now(), 'CREATE_USER', 'Crear usuario', 'b71340e8-b44b-477f-8475-af42c580668b'),
('35c2c191-8d17-4dfb-81a3-2137bdf2a981', now(), now(), 'EDIT_ROLE', 'Editar rol', 'f58bbc71-0082-414e-ba52-666427bce16e'),
('e4a82feb-8e54-415c-aa21-572ab5d641a3', now(), now(), 'GET_ROLE', 'Listar roles', 'f58bbc71-0082-414e-ba52-666427bce16e'),
('bc02b771-4b33-494f-b629-d98d1c843cf3', now(), now(), 'CREATE_ROLE', 'Crear rol', 'f58bbc71-0082-414e-ba52-666427bce16e'),
('4b8111c9-3c22-4e8c-babc-f09d5a95ea8f', now(), now(), 'DELETE_ROLE', 'Eliminar rol', 'f58bbc71-0082-414e-ba52-666427bce16e'),
('721a25cb-c3f5-4e85-9389-f259c8d57cd1', now(), now(), 'GET_POTENTIAL_VOTER', 'Listar posibles votantes', '7f87f369-b46f-4e45-9380-51152c2d352d'),
('f39d68a9-a8e8-466e-a008-1d09d8078ef7', now(), now(), 'CREATE_POTENTIAL_VOTER', 'Crear posible votante', '7f87f369-b46f-4e45-9380-51152c2d352d'),
('0d7f8597-df66-42e1-b468-aaf38bb496ef', now(), now(), 'EDIT_POTENTIAL_VOTER', 'Editar posible votante', '7f87f369-b46f-4e45-9380-51152c2d352d');
