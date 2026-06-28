import os

db_init_dir = r"c:\Users\Aneliss\Desktop\kinetocare\db-init"
prog_file = os.path.join(db_init_dir, "programari_service.sql")
pac_file = os.path.join(db_init_dir, "pacienti_service.sql")
mock_data_file = os.path.join(db_init_dir, "mock-data-june-july.sql")

# 1. New SQL for programari
new_programari_sql = """
-- ==============================================================================
-- RICH CLINICAL MOCK DATA FOR ALL REMAINING PATIENTS (JUNE)
-- ==============================================================================
INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
-- Gh Freya (Keycloak: 2733282b-1ee8-4afb-827a-aa0ff0b98f61, Terapeut: Clara Maria, Locatie: 1)
(200, b'0', b'1', '2026-05-25 09:00:00.000000', '2026-06-08', 50, 1, NULL, '10:00:00.000000', '10:50:00.000000', '2733282b-1ee8-4afb-827a-aa0ff0b98f61', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-08 10:55:00.000000', '2733282b-1ee8-4afb-827a-aa0ff0b98f61', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
(201, b'0', b'1', '2026-05-25 09:05:00.000000', '2026-06-12', 50, 1, NULL, '10:00:00.000000', '10:50:00.000000', '2733282b-1ee8-4afb-827a-aa0ff0b98f61', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-12 10:55:00.000000', '2733282b-1ee8-4afb-827a-aa0ff0b98f61', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
(202, b'0', b'1', '2026-05-25 09:10:00.000000', '2026-06-15', 50, 1, NULL, '10:00:00.000000', '10:50:00.000000', '2733282b-1ee8-4afb-827a-aa0ff0b98f61', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-15 10:55:00.000000', '2733282b-1ee8-4afb-827a-aa0ff0b98f61', '496b7af4-d378-418d-8d5f-5647ed31d11d'),

-- Popescu Alina (Keycloak: 072ee168-7759-4613-96e1-226f0d7277ea, Terapeut: Clara Maria, Locatie: 1)
(203, b'0', b'1', '2026-05-25 09:15:00.000000', '2026-06-08', 50, 1, NULL, '11:00:00.000000', '11:50:00.000000', '072ee168-7759-4613-96e1-226f0d7277ea', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-08 11:55:00.000000', '072ee168-7759-4613-96e1-226f0d7277ea', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
(204, b'0', b'1', '2026-05-25 09:20:00.000000', '2026-06-12', 50, 1, NULL, '11:00:00.000000', '11:50:00.000000', '072ee168-7759-4613-96e1-226f0d7277ea', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-12 11:55:00.000000', '072ee168-7759-4613-96e1-226f0d7277ea', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
(205, b'0', b'1', '2026-05-25 09:25:00.000000', '2026-06-15', 50, 1, NULL, '11:00:00.000000', '11:50:00.000000', '072ee168-7759-4613-96e1-226f0d7277ea', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-15 11:55:00.000000', '072ee168-7759-4613-96e1-226f0d7277ea', '496b7af4-d378-418d-8d5f-5647ed31d11d'),

-- Popescu Andrei (Keycloak: e61befe0-0110-45b7-9a31-8f433b113909, Terapeut: Simona Ilie, Locatie: 2)
(206, b'0', b'1', '2026-05-25 09:30:00.000000', '2026-06-09', 50, 2, NULL, '10:00:00.000000', '10:50:00.000000', 'e61befe0-0110-45b7-9a31-8f433b113909', 100.00, b'0', 6, 'FINALIZATA', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5', 'Kinetoterapie - Ședință individuală', '2026-06-09 10:55:00.000000', 'e61befe0-0110-45b7-9a31-8f433b113909', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5'),
(207, b'0', b'1', '2026-05-25 09:35:00.000000', '2026-06-11', 50, 2, NULL, '10:00:00.000000', '10:50:00.000000', 'e61befe0-0110-45b7-9a31-8f433b113909', 100.00, b'0', 6, 'FINALIZATA', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5', 'Kinetoterapie - Ședință individuală', '2026-06-11 10:55:00.000000', 'e61befe0-0110-45b7-9a31-8f433b113909', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5'),
(208, b'0', b'1', '2026-05-25 09:40:00.000000', '2026-06-16', 50, 2, NULL, '10:00:00.000000', '10:50:00.000000', 'e61befe0-0110-45b7-9a31-8f433b113909', 100.00, b'0', 6, 'FINALIZATA', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5', 'Kinetoterapie - Ședință individuală', '2026-06-16 10:55:00.000000', 'e61befe0-0110-45b7-9a31-8f433b113909', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5'),

-- Gheorghe Elena (Keycloak: 77d841ba-ba92-4d98-a6bd-873c58fbc440, Terapeut: Vlad Radu, Locatie: 2)
-- Shifuit la 11:10 pentru a asigura buffer de 10 min dupa sedinta lui Vlad de la 10:00-11:00
(209, b'0', b'1', '2026-05-25 09:45:00.000000', '2026-06-09', 50, 2, NULL, '11:10:00.000000', '12:00:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, b'0', 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Ședință individuală', '2026-06-09 12:05:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', '51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(210, b'0', b'1', '2026-05-25 09:50:00.000000', '2026-06-11', 50, 2, NULL, '11:10:00.000000', '12:00:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, b'0', 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Ședință individuală', '2026-06-11 12:05:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', '51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(211, b'0', b'1', '2026-05-25 09:55:00.000000', '2026-06-16', 50, 2, NULL, '11:10:00.000000', '12:00:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, b'0', 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Ședință individuală', '2026-06-16 12:05:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', '51abc54c-0c2f-4f0a-aead-bdaa9627661e'),

-- Dobre Daniel (Keycloak: 5ff54c13-8ec1-40b3-b147-bbf2ad48fb86, Terapeut: 69f7e2ae-39c0-4506-8558-d782493821a0, Locatie: 5)
-- Are evaluare initiala pe 8 iunie
(212, b'1', b'1', '2026-05-25 10:00:00.000000', '2026-06-08', 30, 5, NULL, '09:00:00.000000', '09:30:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 150.00, b'1', 1, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Evaluare - Evaluare Inițială', '2026-06-08 09:35:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', '69f7e2ae-39c0-4506-8558-d782493821a0'),
(213, b'0', b'1', '2026-06-08 10:00:00.000000', '2026-06-10', 50, 5, NULL, '09:00:00.000000', '09:50:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 100.00, b'0', 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Ședință individuală', '2026-06-10 09:55:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', '69f7e2ae-39c0-4506-8558-d782493821a0'),
(214, b'0', b'1', '2026-06-08 10:05:00.000000', '2026-06-12', 50, 5, NULL, '09:00:00.000000', '09:50:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 100.00, b'0', 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Ședință individuală', '2026-06-12 09:55:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', '69f7e2ae-39c0-4506-8558-d782493821a0'),

-- Dumitrescu Ana (Keycloak: ac0ef5eb-34df-4a8c-8f4a-e879428aed2d, Terapeut: Sara Munteanu, Locatie: 13)
-- Are evaluare initiala pe 8 iunie
(215, b'1', b'1', '2026-05-25 10:10:00.000000', '2026-06-08', 30, 13, NULL, '08:00:00.000000', '08:30:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 150.00, b'1', 1, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Evaluare - Evaluare Inițială', '2026-06-08 08:35:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 'd407ab1c-7554-456d-9726-3f5571fa8fd0'),
(216, b'0', b'1', '2026-06-08 09:00:00.000000', '2026-06-10', 50, 13, NULL, '08:00:00.000000', '08:50:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-06-10 08:55:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 'd407ab1c-7554-456d-9726-3f5571fa8fd0'),
(217, b'0', b'1', '2026-06-08 09:05:00.000000', '2026-06-12', 50, 13, NULL, '08:00:00.000000', '08:50:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-06-12 08:55:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 'd407ab1c-7554-456d-9726-3f5571fa8fd0'),

-- Tudor Robert (Keycloak: 6da1727a-5388-4bfb-b845-d96485c61498, Terapeut: Sara Munteanu, Locatie: 13)
-- Shifuit la 10:00 pentru a asigura buffer dupa sedintele Sarei cu Maria Ionescu (09:00-09:50)
(218, b'0', b'1', '2026-05-25 10:15:00.000000', '2026-06-08', 50, 13, NULL, '10:00:00.000000', '10:50:00.000000', '6da1727a-5388-4bfb-b845-d96485c61498', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-06-08 10:55:00.000000', '6da1727a-5388-4bfb-b845-d96485c61498', 'd407ab1c-7554-456d-9726-3f5571fa8fd0'),
(219, b'0', b'1', '2026-05-25 10:20:00.000000', '2026-06-10', 50, 13, NULL, '10:00:00.000000', '10:50:00.000000', '6da1727a-5388-4bfb-b845-d96485c61498', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-06-10 10:55:00.000000', '6da1727a-5388-4bfb-b845-d96485c61498', 'd407ab1c-7554-456d-9726-3f5571fa8fd0'),
(220, b'0', b'1', '2026-05-25 10:25:00.000000', '2026-06-12', 50, 13, NULL, '10:00:00.000000', '10:50:00.000000', '6da1727a-5388-4bfb-b845-d96485c61498', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-06-12 10:55:00.000000', '6da1727a-5388-4bfb-b845-d96485c61498', 'd407ab1c-7554-456d-9726-3f5571fa8fd0'),

-- Marin Cristian (Keycloak: 6c400be7-84b0-40f9-8854-b90e6c8358d9, Terapeut: cb3ac7f5-4730-44cf-a053-d17f451f4b02, Locatie: 11)
(221, b'0', b'1', '2026-05-25 10:30:00.000000', '2026-06-09', 50, 11, NULL, '09:00:00.000000', '09:50:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, b'0', 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Ședință individuală', '2026-06-09 09:55:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02'),
(222, b'0', b'1', '2026-05-25 10:35:00.000000', '2026-06-12', 50, 11, NULL, '09:00:00.000000', '09:50:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, b'0', 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Ședință individuală', '2026-06-12 09:55:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02'),
(223, b'0', b'1', '2026-05-25 10:40:00.000000', '2026-06-15', 50, 11, NULL, '09:00:00.000000', '09:50:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, b'0', 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Ședință individuală', '2026-06-15 09:55:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02'),

-- Sorin Tudor (Keycloak: 487da990-f8ed-437f-b5d9-e9e2f5aef199, Terapeut: 69f7e2ae-39c0-4506-8558-d782493821a0, Locatie: 5)
(224, b'0', b'1', '2026-05-25 10:45:00.000000', '2026-06-09', 50, 5, NULL, '10:00:00.000000', '10:50:00.000000', '487da990-f8ed-437f-b5d9-e9e2f5aef199', 100.00, b'0', 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Ședință individuală', '2026-06-09 10:55:00.000000', '487da990-f8ed-437f-b5d9-e9e2f5aef199', '69f7e2ae-39c0-4506-8558-d782493821a0'),
(225, b'0', b'1', '2026-05-25 10:50:00.000000', '2026-06-12', 50, 5, NULL, '10:00:00.000000', '10:50:00.000000', '487da990-f8ed-437f-b5d9-e9e2f5aef199', 100.00, b'0', 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Ședință individuală', '2026-06-12 10:55:00.000000', '487da990-f8ed-437f-b5d9-e9e2f5aef199', '69f7e2ae-39c0-4506-8558-d782493821a0'),
(226, b'0', b'1', '2026-05-25 10:55:00.000000', '2026-06-15', 50, 5, NULL, '10:00:00.000000', '10:50:00.000000', '487da990-f8ed-437f-b5d9-e9e2f5aef199', 100.00, b'0', 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Ședință individuală', '2026-06-15 10:55:00.000000', '487da990-f8ed-437f-b5d9-e9e2f5aef199', '69f7e2ae-39c0-4506-8558-d782493821a0'),

-- Stan Daniela (Keycloak: 235aa647-1c6c-479f-aa26-011539b51b73, Terapeut: Clara Maria, Locatie: 2)
-- Are evaluare initiala pe 8 iunie
(227, b'1', b'1', '2026-05-25 11:00:00.000000', '2026-06-08', 30, 2, NULL, '12:00:00.000000', '12:30:00.000000', '235aa647-1c6c-479f-aa26-011539b51b73', 150.00, b'1', 1, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Evaluare - Evaluare Inițială', '2026-06-08 12:35:00.000000', '235aa647-1c6c-479f-aa26-011539b51b73', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
(228, b'0', b'1', '2026-06-08 13:00:00.000000', '2026-06-12', 50, 2, NULL, '12:00:00.000000', '12:50:00.000000', '235aa647-1c6c-479f-aa26-011539b51b73', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-12 12:55:00.000000', '235aa647-1c6c-479f-aa26-011539b51b73', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
(229, b'0', b'1', '2026-06-08 13:05:00.000000', '2026-06-15', 50, 2, NULL, '12:00:00.000000', '12:50:00.000000', '235aa647-1c6c-479f-aa26-011539b51b73', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-15 12:55:00.000000', '235aa647-1c6c-479f-aa26-011539b51b73', '496b7af4-d378-418d-8d5f-5647ed31d11d'),

-- Popescu Ana-Maria (Keycloak: 9f90d398-f030-4ce4-bfee-033758993378, Terapeut: Clara Maria, Locatie: 1)
-- Are evaluare initiala pe 8 iunie
(230, b'1', b'1', '2026-05-25 11:10:00.000000', '2026-06-08', 30, 1, NULL, '13:00:00.000000', '13:30:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', 150.00, b'1', 1, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Evaluare - Evaluare Inițială', '2026-06-08 13:35:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
(231, b'0', b'1', '2026-06-08 14:00:00.000000', '2026-06-12', 50, 1, NULL, '13:00:00.000000', '13:50:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-12 13:55:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
(232, b'0', b'1', '2026-06-08 14:05:00.000000', '2026-06-15', 50, 1, NULL, '13:00:00.000000', '13:50:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-15 13:55:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', '496b7af4-d378-418d-8d5f-5647ed31d11d');
""";

# 2. New SQL for evaluari
new_evaluari_sql = """
-- ==============================================================================
-- EVALUARI CLINICE INIȚIALE PENTRU PACIENȚI NOI (JUNE)
-- ==============================================================================
INSERT INTO `evaluari` (`id`, `created_at`, `data`, `diagnostic`, `observatii`, `pacient_keycloak_id`, `programare_id`, `sedinte_recomandate`, `serviciu_recomandat_id`, `terapeut_keycloak_id`, `tip`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
-- Dobre Daniel
(30, '2026-06-08 09:35:00.000000', '2026-06-08', 'Cervicobrahialgie stângă', 'Contractură musculară la nivelul trapezului superior stâng și musculaturii scalene. Mobilitate cervicală redusă în rotație. Recomand un plan de 8 ședințe de fizioterapie.', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 212, 8, 6, '69f7e2ae-39c0-4506-8558-d782493821a0', 'INITIALA', '2026-06-08 09:35:00.000000', '69f7e2ae-39c0-4506-8558-d782493821a0', '69f7e2ae-39c0-4506-8558-d782493821a0'),
-- Dumitrescu Ana
(31, '2026-06-08 08:35:00.000000', '2026-06-08', 'Scolioză toraco-lombară', 'Asimetrie de umeri și bazin, rotație vertebrală vizibilă la testul Adams. Recomand un plan intens de kinetoterapie individuală (10 ședințe) axat pe metoda Schroth.', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 215, 10, 6, 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'INITIALA', '2026-06-08 08:35:00.000000', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'd407ab1c-7554-456d-9726-3f5571fa8fd0'),
-- Stan Daniela
(32, '2026-06-08 12:35:00.000000', '2026-06-08', 'Tendinită rotuliană dreaptă', 'Durere localizată la polul inferior al rotulei drepte, exacerbată la palparea tendonului rotulian. Mobilitate normală. Recomand 6 ședințe de recuperare.', '235aa647-1c6c-479f-aa26-011539b51b73', 227, 6, 6, '496b7af4-d378-418d-8d5f-5647ed31d11d', 'INITIALA', '2026-06-08 12:35:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
-- Popescu Ana-Maria
(33, '2026-06-08 13:35:00.000000', '2026-06-08', 'Cifoză dorsală accentuată', 'Postură cifotică rigidă, rigiditate în hiperextensie dorsală. Spasm muscular pe erectorii spinali. Recomand 8 ședințe de reeducare posturală.', '9f90d398-f030-4ce4-bfee-033758993378', 230, 8, 6, '496b7af4-d378-418d-8d5f-5647ed31d11d', 'INITIALA', '2026-06-08 13:35:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d');
""";

# 3. New SQL for evolutii
new_evolutii_sql = """
-- ==============================================================================
-- EVOLUTII CLINICE PENTRU PACIENTI IN TIMPUL SEDINTELOR (JUNE)
-- ==============================================================================
INSERT INTO `evolutii` (`id`, `created_at`, `observatii`, `pacient_keycloak_id`, `terapeut_keycloak_id`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
-- Gh Freya (Clara Maria)
(100, '2026-06-08 10:55:00.000000', 'Ședință axată pe tonifiere musculară paravertebrală. Răspuns clinic bun, pacientul prezintă o postură mai stabilă la efort.', '2733282b-1ee8-4afb-827a-aa0ff0b98f61', '496b7af4-d378-418d-8d5f-5647ed31d11d', '2026-06-08 10:55:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
(101, '2026-06-12 10:55:00.000000', 'Lucru pe propriocepție și echilibru. Control motor foarte bun la corecții posturale active la oglindă.', '2733282b-1ee8-4afb-827a-aa0ff0b98f61', '496b7af4-d378-418d-8d5f-5647ed31d11d', '2026-06-12 10:55:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
(102, '2026-06-15 10:55:00.000000', 'Stretching activ al musculaturii anterioare de trunchi și core-stability. Starea generală este excelentă, fără dureri.', '2733282b-1ee8-4afb-827a-aa0ff0b98f61', '496b7af4-d378-418d-8d5f-5647ed31d11d', '2026-06-15 10:55:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d'),

-- Popescu Alina (Clara Maria)
(103, '2026-06-08 11:55:00.000000', 'Efectuat mobilizări pasive cervicale. Contractura pe mușchiul trapez este redusă semnificativ după masajul terapeutic.', '072ee168-7759-4613-96e1-226f0d7277ea', '496b7af4-d378-418d-8d5f-5647ed31d11d', '2026-06-08 11:55:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
(104, '2026-06-12 11:55:00.000000', 'Ședință bazată pe izometrie cervicală și corectare posturală activă. Toleranță excelentă la efort.', '072ee168-7759-4613-96e1-226f0d7277ea', '496b7af4-d378-418d-8d5f-5647ed31d11d', '2026-06-12 11:55:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
(105, '2026-06-15 11:55:00.000000', 'Lucru pe extensii controlate din patrupedie și stretching active. Pacienta raportează absența crizelor dureroase.', '072ee168-7759-4613-96e1-226f0d7277ea', '496b7af4-d378-418d-8d5f-5647ed31d11d', '2026-06-15 11:55:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d'),

-- Popescu Andrei (Simona Ilie)
(106, '2026-06-09 10:55:00.000000', 'Exerciții respiratorii și corecție posturală din așezat. Spatele rotund tinde să se îndrepte, cooperare deplină din partea pacientului.', 'e61befe0-0110-45b7-9a31-8f433b113909', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5', '2026-06-09 10:55:00.000000', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5'),
(107, '2026-06-11 10:55:00.000000', 'Am adăugat exerciții active de extensie pe burtă (poziția sfinxului). Coordonare posturală excelentă la oglindă.', 'e61befe0-0110-45b7-9a31-8f433b113909', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5', '2026-06-11 10:55:00.000000', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5'),
(108, '2026-06-16 10:55:00.000000', 'Tonifiere musculară scapulară cu benzi elastice ușoare. Pacientul se simte mult mai stăpân pe mișcările dinamice.', 'e61befe0-0110-45b7-9a31-8f433b113909', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5', '2026-06-16 10:55:00.000000', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5'),

-- Gheorghe Elena (Vlad Radu)
-- Notele se inregistreaza la 12:05 dupa sedinta shifuita la 11:10-12:00
(109, '2026-06-09 12:05:00.000000', 'Ședință axată pe decompresie lombară și exerciții pe saltea pentru core. Spasmul lombar a scăzut semnificativ.', '77d841ba-ba92-4d98-a6bd-873c58fbc440', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', '2026-06-09 12:05:00.000000', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', '51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(110, '2026-06-11 12:05:00.000000', 'Mobilizări active și stretching pe lanțul fascial posterior. Mobilitatea în flexie a trunchiului a crescut cu 10 grade.', '77d841ba-ba92-4d98-a6bd-873c58fbc440', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', '2026-06-11 12:05:00.000000', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', '51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(111, '2026-06-16 12:05:00.000000', 'Am introdus exerciții posturale dinamice. Pacienta se simte excelent, având mobilitate normală și absența totală a junghiurilor.', '77d841ba-ba92-4d98-a6bd-873c58fbc440', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', '2026-06-16 12:05:00.000000', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', '51abc54c-0c2f-4f0a-aead-bdaa9627661e'),

-- Dobre Daniel (69f7e2ae-39c0-4506-8558-d782493821a0)
(112, '2026-06-10 09:55:00.000000', 'Prima ședință post-evaluare. Am lucrat pe mobilizări pasive cervicale și tracțiuni ușoare. Durerea iradiată în braț a scăzut la nivel mediu.', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', '69f7e2ae-39c0-4506-8558-d782493821a0', '2026-06-10 09:55:00.000000', '69f7e2ae-39c0-4506-8558-d782493821a0', '69f7e2ae-39c0-4506-8558-d782493821a0'),
(113, '2026-06-12 09:55:00.000000', 'Tonifiere a musculaturii scapulare și stabilizatorilor umerilor cu bandă elastică. Mobilitatea brațului stâng este crescută, fără parestezii.', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', '69f7e2ae-39c0-4506-8558-d782493821a0', '2026-06-12 09:55:00.000000', '69f7e2ae-39c0-4506-8558-d782493821a0', '69f7e2ae-39c0-4506-8558-d782493821a0'),

-- Dumitrescu Ana (Sara Munteanu)
(114, '2026-06-10 08:55:00.000000', 'Inițiat programul Schroth. Exerciții de respirație tridimensională și conștientizare a posturii asimetrice. Pacienta depune un efort deosebit.', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', '2026-06-10 08:55:00.000000', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'd407ab1c-7554-456d-9726-3f5571fa8fd0'),
(115, '2026-06-12 08:55:00.000000', 'Am continuat cu elongații din atârnat și auto-corecție activă la oglindă. Rezistența musculară a spatelui s-a îmbunătățit vizibil.', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', '2026-06-12 08:55:00.000000', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'd407ab1c-7554-456d-9726-3f5571fa8fd0'),

-- Tudor Robert (Sara Munteanu)
-- Notele se inregistreaza la 10:55 dupa sedintele shifuite la 10:00-10:50
(116, '2026-06-08 10:55:00.000000', 'Ședință intensă de tonifiere musculară a trenului inferior (propriocepție gleznă). Stabilitate bună pe suprafețe instabile.', '6da1727a-5388-4bfb-b845-d96485c61498', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', '2026-06-08 10:55:00.000000', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'd407ab1c-7554-456d-9726-3f5571fa8fd0'),
(117, '2026-06-10 10:55:00.000000', 'Săriturile pe stepper nu mi-au provocat nicio durere. Gleznă super stabilă la aterizare, control muscular excelent.', '6da1727a-5388-4bfb-b845-d96485c61498', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', '2026-06-10 10:55:00.000000', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'd407ab1c-7554-456d-9726-3f5571fa8fd0'),
(118, '2026-06-12 10:55:00.000000', 'Am încheiat ciclul de recuperare. Stabilitate dinamică perfectă a gleznei, mobilitate normală fără jenă dureroasă la efort maxim.', '6da1727a-5388-4bfb-b845-d96485c61498', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', '2026-06-12 10:55:00.000000', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'd407ab1c-7554-456d-9726-3f5571fa8fd0'),

-- Marin Cristian (cb3ac7f5-4730-44cf-a053-d17f451f4b02)
(119, '2026-06-09 09:55:00.000000', 'Mobilizări active de genunchi (flexie/extensie) cu rezistență elastică medie. Amplitudinea articulară a crescut cu 5 grade, jena a scăzut.', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', '2026-06-09 09:55:00.000000', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02'),
(120, '2026-06-12 09:55:00.000000', 'Tonifierea mușchiului vast medial (VMO) prin izometrie din sprijin pe un picior. Control postural optim la rotări.', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', '2026-06-12 09:55:00.000000', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02'),
(121, '2026-06-15 09:55:00.000000', 'Ședință intensă de propriocepție pe perne instabile. Coordonare foarte bună, genunchiul drept este perfect stabil la efort prelungit.', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', '2026-06-15 09:55:00.000000', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02'),

-- Sorin Tudor (69f7e2ae-39c0-4506-8558-d782493821a0)
(122, '2026-06-09 10:55:00.000000', 'Stretching intens al fasciei plantare și musculaturii posterioare a gambei. Durerea matinală s-a retras considerabil.', '487da990-f8ed-437f-b5d9-e9e2f5aef199', '69f7e2ae-39c0-4506-8558-d782493821a0', '2026-06-09 10:55:00.000000', '69f7e2ae-39c0-4506-8558-d782493821a0', '69f7e2ae-39c0-4506-8558-d782493821a0'),
(123, '2026-06-12 10:55:00.000000', 'Mobilizări active și stretching profund. Pacientul relateaza o stare generală mult mai bună, mersul este fluid și nedureros.', '487da990-f8ed-437f-b5d9-e9e2f5aef199', '69f7e2ae-39c0-4506-8558-d782493821a0', '2026-06-12 10:55:00.000000', '69f7e2ae-39c0-4506-8558-d782493821a0', '69f7e2ae-39c0-4506-8558-d782493821a0'),
(124, '2026-06-15 10:55:00.000000', 'Ședință finalizată. Propriocepție fină a piciorului pe suprafețe diverse, durerea la mers este complet absentă.', '487da990-f8ed-437f-b5d9-e9e2f5aef199', '69f7e2ae-39c0-4506-8558-d782493821a0', '2026-06-15 10:55:00.000000', '69f7e2ae-39c0-4506-8558-d782493821a0', '69f7e2ae-39c0-4506-8558-d782493821a0'),

-- Stan Daniela (Clara Maria)
(125, '2026-06-12 12:55:00.000000', 'Mobilizări pasive și masaj al tendonului rotulian. Jena dureroasă la aplecare s-a retras semnificativ. Pacienta cooperează deplin.', '235aa647-1c6c-479f-aa26-011539b51b73', '496b7af4-d378-418d-8d5f-5647ed31d11d', '2026-06-12 12:55:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
(126, '2026-06-15 12:55:00.000000', 'Tonifiere a musculaturii cvadricepsului prin exerciții izometrice blânde. Stabilitatea genunchiului drept este la un nivel excelent.', '235aa647-1c6c-479f-aa26-011539b51b73', '496b7af4-d378-418d-8d5f-5647ed31d11d', '2026-06-15 12:55:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d'),

-- Popescu Ana-Maria (Clara Maria)
(127, '2026-06-12 13:55:00.000000', 'Corecție posturală la oglindă și exerciții dinamice pe saltea pentru deschiderea cutiei toracice. Postură global aliniată corect.', '9f90d398-f030-4ce4-bfee-033758993378', '496b7af4-d378-418d-8d5f-5647ed31d11d', '2026-06-12 13:55:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d'),
(128, '2026-06-15 13:55:00.000000', 'Am introdus exerciții de tonifiere musculară profundă a spatelui cu mingea pilates. Coordonare posturală mult superioară.', '9f90d398-f030-4ce4-bfee-033758993378', '496b7af4-d378-418d-8d5f-5647ed31d11d', '2026-06-15 13:55:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d');
""";

# 4. New SQL for relatie_pacient_terapeut (to link Popescu Ana-Maria with Clara Maria)
new_relatie_sql = """
-- ==============================================================================
-- NOI RELATII ACTIVE PACIENT-TERAPEUT (JUNE)
-- ==============================================================================
INSERT INTO `relatie_pacient_terapeut` (`id`, `activa`, `created_at`, `data_inceput`, `data_sfarsit`, `pacient_keycloak_id`, `terapeut_keycloak_id`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
(30, b'1', '2026-06-01 09:00:00.000000', '2026-06-01', NULL, '9f90d398-f030-4ce4-bfee-033758993378', '496b7af4-d378-418d-8d5f-5647ed31d11d', '2026-06-01 09:00:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d');
""";

# 5. New SQL for jurnale in pacienti_service
new_jurnale_sql = """
-- ==============================================================================
-- RICH MOCK DATA FOR ALL REMAINING PATIENTS' JOURNALS (JUNE)
-- ==============================================================================
INSERT INTO `jurnal_pacient` (`id`, `comentarii`, `created_at`, `data`, `dificultate_exercitii`, `nivel_durere`, `nivel_oboseala`, `pacient_id`, `programare_id`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
-- Gh Freya (pacient_id = 1)
(200, 'Prima ședință post-concediu, spatele se simte stabil dar obosit.', '2026-06-08 11:00:00.000000', '2026-06-08', 4, 2, 4, 1, 200, '2026-06-08 11:00:00.000000', '2733282b-1ee8-4afb-827a-aa0ff0b98f61', '2733282b-1ee8-4afb-827a-aa0ff0b98f61'),
(201, 'Exercițiile posturale au fost destul de grele, dar umerii mei s-au relaxat.', '2026-06-12 11:30:00.000000', '2026-06-12', 5, 1, 5, 1, 201, '2026-06-12 11:30:00.000000', '2733282b-1ee8-4afb-827a-aa0ff0b98f61', '2733282b-1ee8-4afb-827a-aa0ff0b98f61'),
(202, 'Control bun postural, mă simt foarte bine și plină de energie.', '2026-06-15 11:15:00.000000', '2026-06-15', 3, 0, 3, 1, 202, '2026-06-15 11:15:00.000000', '2733282b-1ee8-4afb-827a-aa0ff0b98f61', '2733282b-1ee8-4afb-827a-aa0ff0b98f61'),

-- Popescu Alina (pacient_id = 3)
(203, 'Ușoară tensiune cervicală la începutul încălzirii, dar s-a eliberat complet.', '2026-06-08 12:00:00.000000', '2026-06-08', 3, 3, 4, 3, 203, '2026-06-08 12:00:00.000000', '072ee168-7759-4613-96e1-226f0d7277ea', '072ee168-7759-4613-96e1-226f0d7277ea'),
(204, 'Spasmele din zona omoplaților au dispărut, spatele se simte minunat.', '2026-06-12 12:30:00.000000', '2026-06-12', 4, 1, 3, 3, 204, '2026-06-12 12:30:00.000000', '072ee168-7759-4613-96e1-226f0d7277ea', '072ee168-7759-4613-96e1-226f0d7277ea'),
(205, 'Fără nicio durere la finalul antrenamentului medical. Foarte mulțumită.', '2026-06-15 12:00:00.000000', '2026-06-15', 3, 0, 3, 3, 205, '2026-06-15 12:00:00.000000', '072ee168-7759-4613-96e1-226f0d7277ea', '072ee168-7759-4613-96e1-226f0d7277ea'),

-- Popescu Andrei (pacient_id = 7)
(206, 'Dificultate medie la exercițiile posturale, dar respir mult mai ușor.', '2026-06-09 11:00:00.000000', '2026-06-09', 4, 2, 4, 7, 206, '2026-06-09 11:00:00.000000', 'e61befe0-0110-45b7-9a31-8f433b113909', 'e61befe0-0110-45b7-9a31-8f433b113909'),
(207, 'Spatele rotund se îndreaptă mai natural acum, febra musculară este plăcută.', '2026-06-11 11:15:00.000000', '2026-06-11', 4, 1, 3, 7, 207, '2026-06-11 11:15:00.000000', 'e61befe0-0110-45b7-9a31-8f433b113909', 'e61befe0-0110-45b7-9a31-8f433b113909'),
(208, 'Mă simt foarte bine. Exercițiile cu elastice au fost super distractive.', '2026-06-16 11:00:00.000000', '2026-06-16', 3, 0, 2, 7, 208, '2026-06-16 11:00:00.000000', 'e61befe0-0110-45b7-9a31-8f433b113909', 'e61befe0-0110-45b7-9a31-8f433b113909'),

-- Gheorghe Elena (pacient_id = 9)
(209, 'Decompresia lombară de azi a fost extraordinară, spatele s-a eliberat.', '2026-06-09 12:00:00.000000', '2026-06-09', 3, 3, 3, 9, 209, '2026-06-09 12:00:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', '77d841ba-ba92-4d98-a6bd-873c58fbc440'),
(210, 'Mobilitate crescută la aplecare, jena lombară este aproape complet absentă.', '2026-06-11 12:15:00.000000', '2026-06-11', 4, 1, 4, 9, 210, '2026-06-11 12:15:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', '77d841ba-ba92-4d98-a6bd-873c58fbc440'),
(211, 'Sunt complet refăcută! Pot sta la birou 8 ore fără niciun disconfort.', '2026-06-16 12:00:00.000000', '2026-06-16', 3, 0, 2, 9, 211, '2026-06-16 12:00:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', '77d841ba-ba92-4d98-a6bd-873c58fbc440'),

-- Dobre Daniel (pacient_id = 12)
(212, 'Evaluarea a fost detaliată. Am înțeles exact cauzele amorțelii din braț.', '2026-06-08 09:45:00.000000', '2026-06-08', 2, 5, 3, 12, 212, '2026-06-08 09:45:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86'),
(213, 'Amorțeala a scăzut semnificativ după tracțiunile cervicale de azi.', '2026-06-10 10:00:00.000000', '2026-06-10', 4, 3, 4, 12, 213, '2026-06-10 10:00:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86'),
(214, 'Mobilitatea gâtului a revenit aproape total, mă simt mult mai eliberat.', '2026-06-12 10:15:00.000000', '2026-06-12', 4, 1, 3, 12, 214, '2026-06-12 10:15:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86'),

-- Dumitrescu Ana (pacient_id = 13)
(215, 'Evaluare inițială. Am aflat că am scolioză, dar sper că metoda Schroth mă va ajuta.', '2026-06-08 08:45:00.000000', '2026-06-08', 3, 4, 4, 13, 215, '2026-06-08 08:45:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d'),
(216, 'Exercițiile de respirație Schroth sunt foarte grele dar simt cum lucrează spatele.', '2026-06-10 09:00:00.000000', '2026-06-10', 6, 3, 5, 13, 216, '2026-06-10 09:00:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d'),
(217, 'Febra musculară este mare, dar spatele meu se simte mult mai drept.', '2026-06-12 09:15:00.000000', '2026-06-12', 5, 2, 4, 13, 217, '2026-06-12 09:15:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d'),

-- Tudor Robert (pacient_id = 15)
(218, 'Glezna se simte puternică, exercițiile de echilibru au fost foarte solicitante.', '2026-06-08 11:00:00.000000', '2026-06-08', 4, 2, 4, 15, 218, '2026-06-08 11:00:00.000000', '6da1727a-5388-4bfb-b845-d96485c61498', '6da1727a-5388-4bfb-b845-d96485c61498'),
(219, 'Săriturile pe stepper nu mi-au provocat nicio durere. Gleznă super stabilă.', '2026-06-10 11:15:00.000000', '2026-06-10', 5, 1, 4, 15, 219, '2026-06-10 11:15:00.000000', '6da1727a-5388-4bfb-b845-d96485c61498', '6da1727a-5388-4bfb-b845-d96485c61498'),
(220, 'Am finalizat recuperarea! Gleznă complet refăcută și perfect mobilă.', '2026-06-12 11:00:00.000000', '2026-06-12', 3, 0, 3, 15, 220, '2026-06-12 11:00:00.000000', '6da1727a-5388-4bfb-b845-d96485c61498', '6da1727a-5388-4bfb-b845-d96485c61498'),

-- Marin Cristian (pacient_id = 16)
(221, 'Flexia genunchiului este destul de rigidă, dar durerea a cedat după gheață.', '2026-06-09 10:00:00.000000', '2026-06-09', 4, 3, 4, 16, 221, '2026-06-09 10:00:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', '6c400be7-84b0-40f9-8854-b90e6c8358d9'),
(222, 'Am lucrat serios pe placă, genunchiul se simte mult mai stabil la rotiri.', '2026-06-12 10:15:00.000000', '2026-06-12', 5, 2, 4, 16, 222, '2026-06-12 10:15:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', '6c400be7-84b0-40f9-8854-b90e6c8358d9'),
(223, 'Fără durere la exercițiile de propriocepție, stabilitate maximă a genunchiului.', '2026-06-15 10:00:00.000000', '2026-06-15', 4, 0, 3, 16, 223, '2026-06-15 10:00:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', '6c400be7-84b0-40f9-8854-b90e6c8358d9'),

-- Sorin Tudor (pacient_id = 17)
(224, 'Stretchingul intens pe talpă m-a durut, dar masajul a adus ușurare.', '2026-06-09 11:00:00.000000', '2026-06-09', 4, 4, 4, 17, 224, '2026-06-09 11:00:00.000000', '487da990-f8ed-437f-b5d9-e9e2f5aef199', '487da990-f8ed-437f-b5d9-e9e2f5aef199'),
(225, 'Mă simt mult mai bine, durerea de călcâi de dimineață a scăzut considerabil.', '2026-06-12 11:15:00.000000', '2026-06-12', 3, 2, 3, 17, 225, '2026-06-12 11:15:00.000000', '487da990-f8ed-437f-b5d9-e9e2f5aef199', '487da990-f8ed-437f-b5d9-e9e2f5aef199'),
(226, 'Pot merge pe stradă complet fără durere! Recuperare finalizată cu succes.', '2026-06-15 11:00:00.000000', '2026-06-15', 2, 0, 2, 17, 226, '2026-06-15 11:00:00.000000', '487da990-f8ed-437f-b5d9-e9e2f5aef199', '487da990-f8ed-437f-b5d9-e9e2f5aef199'),

-- Stan Daniela (pacient_id = 19)
(227, 'Evaluare inițială. Sper ca exercițiile să reducă jena din genunchiul drept.', '2026-06-08 12:45:00.000000', '2026-06-08', 3, 4, 3, 19, 227, '2026-06-08 12:45:00.000000', '235aa647-1c6c-479f-aa26-011539b51b73', '235aa647-1c6c-479f-aa26-011539b51b73'),
(228, 'Tonifierea musculară a coapsei a mers bine, genunchiul se simte stabil.', '2026-06-12 13:30:00.000000', '2026-06-12', 4, 2, 4, 19, 228, '2026-06-12 13:30:00.000000', '235aa647-1c6c-479f-aa26-011539b51b73', '235aa647-1c6c-479f-aa26-011539b51b73'),
(229, 'Fără nicio durere la mers sau la îndoirea genunchiului, stare generală super.', '2026-06-15 13:00:00.000000', '2026-06-15', 3, 0, 3, 19, 229, '2026-06-15 13:00:00.000000', '235aa647-1c6c-479f-aa26-011539b51b73', '235aa647-1c6c-479f-aa26-011539b51b73'),

-- Popescu Ana-Maria (pacient_id = 22)
(230, 'Evaluare inițială. Am aflat că rigiditatea dorsală se datorează posturii la birou.', '2026-06-08 13:45:00.000000', '2026-06-08', 3, 4, 4, 22, 230, '2026-06-08 13:45:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', '9f90d398-f030-4ce4-bfee-033758993378'),
(231, 'Exercițiile de deschidere toracală au adus o eliberare instantanee a spatelui.', '2026-06-12 14:30:00.000000', '2026-06-12', 4, 2, 4, 22, 231, '2026-06-12 14:30:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', '9f90d398-f030-4ce4-bfee-033758993378'),
(232, 'Mă simt mult mai înaltă și dreaptă acum, rigiditatea spinală a dispărut.', '2026-06-15 14:00:00.000000', '2026-06-15', 3, 0, 3, 22, 232, '2026-06-15 14:00:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', '9f90d398-f030-4ce4-bfee-033758993378');
""";

def inject_additional_data():
    print("Reading programari_service.sql...")
    with open(prog_file, "r", encoding="utf-8") as f:
        prog_content = f.read()
        
    unlock_str = "UNLOCK TABLES;"
        
    # Append programari
    target_prog_end = "/*!40000 ALTER TABLE `programari` ENABLE KEYS */;"
    if target_prog_end in prog_content:
        if "Gh Freya (Keycloak: 2733282b-1ee8-4afb-827a-aa0ff0b98f61" not in prog_content:
            lock_str = "LOCK TABLES `programari` WRITE;"
            start_pos = prog_content.find(lock_str)
            if start_pos != -1:
                end_pos = prog_content.find(unlock_str, start_pos)
                if end_pos != -1:
                    block = prog_content[start_pos:end_pos]
                    if target_prog_end in block:
                        new_block = block.replace(target_prog_end, new_programari_sql + target_prog_end, 1)
                        prog_content = prog_content[:start_pos] + new_block + prog_content[end_pos:]
                        print("Successfully injected new programari.")
            
    # Append evaluari (as complete independent statement)
    target_eval_end = "/*!40000 ALTER TABLE `evaluari` ENABLE KEYS */;"
    if target_eval_end in prog_content:
        if "EVALUARI CLINICE INIȚIALE PENTRU PACIENȚI NOI" not in prog_content:
            lock_str = "LOCK TABLES `evaluari` WRITE;"
            start_pos = prog_content.find(lock_str)
            if start_pos != -1:
                end_pos = prog_content.find(unlock_str, start_pos)
                if end_pos != -1:
                    block = prog_content[start_pos:end_pos]
                    if target_eval_end in block:
                        new_block = block.replace(target_eval_end, new_evaluari_sql + target_eval_end, 1)
                        prog_content = prog_content[:start_pos] + new_block + prog_content[end_pos:]
                        print("Successfully injected new evaluari.")
            
    # Append evolutii (as complete independent statement)
    target_evol_end = "/*!40000 ALTER TABLE `evolutii` ENABLE KEYS */;"
    if target_evol_end in prog_content:
        if "EVOLUTII CLINICE PENTRU PACIENTI IN TIMPUL SEDINTELOR" not in prog_content:
            lock_str = "LOCK TABLES `evolutii` WRITE;"
            start_pos = prog_content.find(lock_str)
            if start_pos != -1:
                end_pos = prog_content.find(unlock_str, start_pos)
                if end_pos != -1:
                    block = prog_content[start_pos:end_pos]
                    if target_evol_end in block:
                        new_block = block.replace(target_evol_end, new_evolutii_sql + target_evol_end, 1)
                        prog_content = prog_content[:start_pos] + new_block + prog_content[end_pos:]
                        print("Successfully injected new evolutii.")

    # Append relatii (as complete independent statement)
    target_rel_end = "/*!40000 ALTER TABLE `relatie_pacient_terapeut` ENABLE KEYS */;"
    if target_rel_end in prog_content:
        if "NOI RELATII ACTIVE PACIENT-TERAPEUT" not in prog_content:
            lock_str = "LOCK TABLES `relatie_pacient_terapeut` WRITE;"
            start_pos = prog_content.find(lock_str)
            if start_pos != -1:
                end_pos = prog_content.find(unlock_str, start_pos)
                if end_pos != -1:
                    block = prog_content[start_pos:end_pos]
                    if target_rel_end in block:
                        new_block = block.replace(target_rel_end, new_relatie_sql + target_rel_end, 1)
                        prog_content = prog_content[:start_pos] + new_block + prog_content[end_pos:]
                        print("Successfully injected new relationships.")
            
    with open(prog_file, "w", encoding="utf-8") as f:
        f.write(prog_content)
        
    # 2. Append pacienti jurnale
    print("Reading pacienti_service.sql...")
    with open(pac_file, "r", encoding="utf-8") as f:
        pac_content = f.read()
        
    target_pac_end = "/*!40000 ALTER TABLE `jurnal_pacient` ENABLE KEYS */;"
    if target_pac_end in pac_content:
        if "Gh Freya (pacient_id = 1)" not in pac_content:
            lock_str = "LOCK TABLES `jurnal_pacient` WRITE;"
            start_pos = pac_content.find(lock_str)
            if start_pos != -1:
                end_pos = pac_content.find(unlock_str, start_pos)
                if end_pos != -1:
                    block = pac_content[start_pos:end_pos]
                    if target_pac_end in block:
                        new_block = block.replace(target_pac_end, new_jurnale_sql + target_pac_end, 1)
                        pac_content = pac_content[:start_pos] + new_block + pac_content[end_pos:]
                        print("Successfully injected new journals.")
            
    with open(pac_file, "w", encoding="utf-8") as f:
        f.write(pac_content)
        
    # 3. Also append to mock-data-june-july.sql just to keep it in sync and documented
    print("Writing to mock-data-june-july.sql...")
    append_mock_sql = f"""
-- ============================================================================
-- ADDITIONAL RICH CLINICAL MOCK DATA FOR ALL REMAINING PATIENTS (JUNE)
-- ============================================================================
USE `programari_service`;

-- Relatii
{new_relatie_sql}

-- Programari
{new_programari_sql}

-- Evaluari
{new_evaluari_sql}

-- Evolutii
{new_evolutii_sql}

USE `pacienti_service`;

-- Jurnale
{new_jurnale_sql}
"""
    with open(mock_data_file, "a", encoding="utf-8") as f:
        f.write(append_mock_sql)
    print("Successfully written to mock-data-june-july.sql.")

if __name__ == "__main__":
    inject_additional_data()
