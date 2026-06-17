INSERT INTO vota_mas.voting_zones (name) VALUES
('ZONA 01'),
('ZONA 02'),
('ZONA 03'),
('ZONA 04'),
('ZONA 05'),
('ZONA 06'),
('ZONA 07'),
('ZONA 08'),
('ZONA 09'),
('ZONA 10'),
('ZONA 90'),
('ZONA 98'),
('ZONA 99')
ON CONFLICT (name) DO NOTHING;
