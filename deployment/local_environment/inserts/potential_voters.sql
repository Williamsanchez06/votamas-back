INSERT INTO vota_mas.potential_voters (
    potential_voter_id,
    identification,
    first_name,
    last_name,
    commune,
    neighborhood,
    polling_place,
    polling_station,
    registration_date,
    assigned_leader_id,
    created_at,
    updated_at
) VALUES
(
    '5b9f1c3b-2f6e-4e90-a9a9-1e7d56f6a001',
    '1000000001',
    'Carlos',
    'Ramirez',
    'Comuna 1',
    'Centro',
    'Institución Educativa Central',
    '12',
    CURRENT_DATE,
    '121e1766-2d53-4f3d-a1e1-8b72f3285e16',
    now(),
    now()
),
(
    '5b9f1c3b-2f6e-4e90-a9a9-1e7d56f6a002',
    '1000000002',
    'Maria',
    'Gomez',
    'Comuna 2',
    'La Esperanza',
    'Colegio Municipal',
    '08',
    CURRENT_DATE,
    '121e1766-2d53-4f3d-a1e1-8b72f3285e16',
    now(),
    now()
);
