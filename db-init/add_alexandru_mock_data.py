import os

db_init_dir = r"c:\Users\Aneliss\Desktop\kinetocare\db-init"
prog_file = os.path.join(db_init_dir, "programari_service.sql")
pac_file = os.path.join(db_init_dir, "pacienti_service.sql")
mock_data_file = os.path.join(db_init_dir, "mock-data-june-july.sql")

# 1. New SQL for programari (complete INSERT INTO statement)
new_programari_sql = """
-- ==============================================================================
-- RICH CLINICAL DATA FOR ALEXANDRU DUMITRESCU'S ACTIVE PATIENTS (JUNE-JULY)
-- ==============================================================================
INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
-- Mihnea Matei (Keycloak: 00179244-a86e-4ade-8e22-3d70af6de57d, Terapeut: Alexandru Dumitrescu, Locatie: 8)
-- Iunie (Cota de 4 sedinte din planul de reevaluare din martie + Reevaluare + 3 sedinte profilactice)
(140, b'0', b'1', '2026-05-25 10:00:00.000000', '2026-06-01', 50, 8, NULL, '15:00:00.000000', '15:50:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-01 15:55:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(141, b'0', b'1', '2026-05-25 10:05:00.000000', '2026-06-03', 50, 8, NULL, '15:00:00.000000', '15:50:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-03 15:55:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(142, b'0', b'1', '2026-05-25 10:10:00.000000', '2026-06-05', 50, 8, NULL, '16:10:00.000000', '17:00:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-05 17:05:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(143, b'0', b'1', '2026-05-25 10:15:00.000000', '2026-06-08', 50, 8, NULL, '15:00:00.000000', '15:50:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-08 15:55:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(144, b'1', b'1', '2026-06-08 16:00:00.000000', '2026-06-10', 30, 8, NULL, '15:00:00.000000', '15:30:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', 100.00, b'0', 2, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Evaluare - Reevaluare', '2026-06-10 15:35:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(145, b'0', b'1', '2026-06-10 16:00:00.000000', '2026-06-15', 50, 8, NULL, '15:00:00.000000', '15:50:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', 110.00, b'0', 8, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Gimnastică profilactică', '2026-06-15 15:55:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(146, b'0', b'1', '2026-06-10 16:05:00.000000', '2026-06-17', 50, 8, NULL, '15:00:00.000000', '15:50:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', 110.00, b'0', 8, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Gimnastică profilactică', '2026-06-17 15:55:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(147, b'0', b'1', '2026-06-10 16:10:00.000000', '2026-06-19', 50, 8, NULL, '15:00:00.000000', '15:50:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', 110.00, b'0', 8, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Gimnastică profilactică', '2026-06-19 15:55:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
-- Iulie (Primele sedinte profilactice viitoare)
(148, b'0', b'0', '2026-06-19 16:00:00.000000', '2026-07-01', 50, 8, NULL, '15:00:00.000000', '15:50:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', 110.00, b'0', 8, 'PROGRAMATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Gimnastică profilactică', '2026-06-19 16:00:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '00179244-a86e-4ade-8e22-3d70af6de57d'),
(149, b'0', b'0', '2026-06-19 16:05:00.000000', '2026-07-03', 50, 8, NULL, '15:00:00.000000', '15:50:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', 110.00, b'0', 8, 'PROGRAMATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Gimnastică profilactică', '2026-06-19 16:05:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '00179244-a86e-4ade-8e22-3d70af6de57d'),

-- Irina Ionescu (Keycloak: b6f3d53a-f2d3-4e2b-b531-d1463a88ef70, Terapeut: Alexandru Dumitrescu, Locatie: 7)
-- Iunie (Reevaluare dupa o perioada de pauza din martie + 5 sedinte Recuperare)
(150, b'1', b'1', '2026-05-25 11:00:00.000000', '2026-06-02', 30, 7, NULL, '10:00:00.000000', '10:30:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 100.00, b'0', 2, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Evaluare - Reevaluare', '2026-06-02 10:35:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(151, b'0', b'1', '2026-06-02 11:00:00.000000', '2026-06-04', 50, 7, NULL, '10:00:00.000000', '10:50:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-04 10:55:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(152, b'0', b'1', '2026-06-02 11:05:00.000000', '2026-06-09', 50, 7, NULL, '10:00:00.000000', '10:50:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-09 10:55:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(153, b'0', b'1', '2026-06-02 11:10:00.000000', '2026-06-11', 50, 7, NULL, '10:00:00.000000', '10:50:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-11 10:55:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(154, b'0', b'1', '2026-06-02 11:15:00.000000', '2026-06-16', 50, 7, NULL, '10:00:00.000000', '10:50:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-16 10:55:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(155, b'0', b'1', '2026-06-02 11:20:00.000000', '2026-06-18', 50, 7, NULL, '10:00:00.000000', '10:50:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-18 10:55:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
-- Iulie (Sedinta viitoare)
(156, b'0', b'0', '2026-06-18 11:00:00.000000', '2026-07-07', 50, 7, NULL, '10:00:00.000000', '10:50:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 100.00, b'0', 6, 'PROGRAMATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-18 11:00:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70'),

-- Mihaela Neagu (Keycloak: 31d03fc1-1800-4a52-9817-0c6b21d78e14, Terapeut: Alexandru Dumitrescu, Locatie: 7)
-- Iunie (Finalizare rest cota de tratament din martie)
(160, b'0', b'1', '2026-05-25 12:00:00.000000', '2026-06-01', 50, 7, NULL, '12:10:00.000000', '13:00:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-01 13:05:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(161, b'0', b'1', '2026-05-25 12:05:00.000000', '2026-06-03', 50, 7, NULL, '11:00:00.000000', '11:50:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-03 11:55:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(162, b'0', b'1', '2026-05-25 12:10:00.000000', '2026-06-05', 50, 7, NULL, '11:00:00.000000', '11:50:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-05 11:55:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(163, b'0', b'1', '2026-06-05 12:00:00.000000', '2026-06-10', 50, 7, NULL, '11:00:00.000000', '11:50:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-10 11:55:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(164, b'0', b'1', '2026-06-05 12:05:00.000000', '2026-06-12', 50, 7, NULL, '11:00:00.000000', '11:50:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-12 11:55:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
-- Iulie (Sedinta viitoare)
(165, b'0', b'0', '2026-06-12 12:00:00.000000', '2026-07-01', 50, 7, NULL, '11:00:00.000000', '11:50:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'PROGRAMATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-12 12:00:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '31d03fc1-1800-4a52-9817-0c6b21d78e14'),

-- Mihai Stan (Keycloak: 5f89ace3-454d-486a-accd-986caa20d84a, Terapeut: Alexandru Dumitrescu, Locatie: 8)
-- Iunie (Finalizare cota tratament)
(170, b'0', b'1', '2026-05-25 13:00:00.000000', '2026-06-02', 50, 8, NULL, '14:00:00.000000', '14:50:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-02 14:55:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(171, b'0', b'1', '2026-05-25 13:05:00.000000', '2026-06-04', 50, 8, NULL, '14:00:00.000000', '14:50:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-04 14:55:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(172, b'0', b'1', '2026-05-25 13:10:00.000000', '2026-06-09', 50, 8, NULL, '14:00:00.000000', '14:50:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-09 14:55:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(173, b'0', b'1', '2026-05-25 13:15:00.000000', '2026-06-11', 50, 8, NULL, '14:00:00.000000', '14:50:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-11 14:55:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
-- Iulie (Sedinta viitoare)
(174, b'0', b'0', '2026-06-11 15:00:00.000000', '2026-07-02', 50, 8, NULL, '14:00:00.000000', '14:50:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, b'0', 6, 'PROGRAMATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-11 15:00:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', '5f89ace3-454d-486a-accd-986caa20d84a');
""";

# 2. New SQL for evaluari (COMPLETE independent INSERT INTO statement with explicit column lists)
new_evaluari_sql = """
-- ==============================================================================
-- EVALUARI CLINICE PENTRU PACIENTII LUI ALEXANDRU DUMITRESCU (JUNE-JULY)
-- ==============================================================================
INSERT INTO `evaluari` (`id`, `created_at`, `data`, `diagnostic`, `observatii`, `pacient_keycloak_id`, `programare_id`, `sedinte_recomandate`, `serviciu_recomandat_id`, `terapeut_keycloak_id`, `tip`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
-- Mihnea Matei reevaluare
(26, '2026-06-10 15:35:00.000000', '2026-06-10', 'Cervicalgie miotensivă - Ameliorată', 'Control postural optim. Forta paravertebrala cervicala refacuta complet. Pacientul incepe planul profilactic de mentinere (3 sedinte recomandate).', '00179244-a86e-4ade-8e22-3d70af6de57d', 144, 3, 8, '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'REEVALUARE', '2026-06-10 15:35:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
-- Irina Ionescu reevaluare (pauza din martie)
(27, '2026-06-02 10:35:00.000000', '2026-06-02', 'Entorsă gleznă grad II - Mobilitate redusă', 'Stabilitatea gleznei a scazut in perioada de inactivitate. Mobilitatea in flexie dorsala este limitata si insotita de jena la 15 grade. Se recomanda 5 sedinte de kinetoterapie pentru proprioceptie.', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 150, 5, 6, '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'REEVALUARE', '2026-06-02 10:35:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af');
""";

# 3. New SQL for evolutii (COMPLETE independent INSERT INTO statement with explicit column lists)
new_evolutii_sql = """
-- ==============================================================================
-- EVOLUTII CLINICE PENTRU PACIENTII LUI ALEXANDRU DUMITRESCU (JUNE-JULY)
-- ==============================================================================
INSERT INTO `evolutii` (`id`, `created_at`, `observatii`, `pacient_keycloak_id`, `terapeut_keycloak_id`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
-- Note de evolutie: Mihnea Matei (Alexandru Dumitrescu)
(50, '2026-06-01 15:55:00.000000', 'Prima sedinta din noul calup de kinetoterapie. Am lucrat pe tonifiere cervicala si mobilizari neurodinamice. Pacientul se simte confortabil, fara dureri.', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-01 15:55:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(51, '2026-06-03 15:55:00.000000', 'Sedinta axata pe corectie posturala la oglinda si izometrie. Control muscular excelent.', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-03 15:55:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(52, '2026-06-05 17:05:00.000000', 'Sedinta reprogramata pentru dupa-amiaza. Am lucrat din patrupedie pe extensii controlate. Fara semne de oboseala cervicala.', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-05 17:05:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(53, '2026-06-08 15:55:00.000000', 'Sedinta 4 finalizata. Forta si mobilitatea paravertebrala sunt la nivel optim. Spasmele pe trapez au disparut complet. Pregatit pentru reevaluare.', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-08 15:55:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(54, '2026-06-15 15:55:00.000000', 'Prima sedinta profilactica (Ciclul II). Gimnastica dinamica usoara pe saltea si exercitii cu mingea pilates. Coordonare posturala excelenta.', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-15 15:55:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(55, '2026-06-17 15:55:00.000000', 'Lucru pe proprioceptie si stretching active. Pacientul relateaza absenta totala a durerilor la birou. Foarte cooperant.', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-17 15:55:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(56, '2026-06-19 15:55:00.000000', 'Ultima sedinta din programul profilactic. Rezistenta musculara excelenta a zonei cervicale. Programul de recuperare s-a incheiat cu succes.', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-19 15:55:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),

-- Note de evolutie: Irina Ionescu (Alexandru Dumitrescu)
(57, '2026-06-04 10:55:00.000000', 'Prima sedinta post-reevaluare. Mobilizari pasive ale gleznei stangi si stretching pe tendonul ahilian. Edemul este absent, dar mobilitatea este redusa.', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-04 10:55:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(58, '2026-06-09 10:55:00.000000', 'Am introdus exercitii de proprioceptie pe perna instabila (din sprijin unipodal). Pacienta are usoare pierderi de echilibru, dar compenseaza rapid.', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-09 10:55:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(59, '2026-06-11 10:55:00.000000', 'Tonifiere a musculaturii peroniere cu banda elastica. Amplitudinea in flexie dorsala a crescut cu 5 grade, jena s-a retras semnificativ.', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-11 10:55:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(60, '2026-06-16 10:55:00.000000', 'Exercitii dinamice (mers pe varfuri si pe calcaie) executate cu control optim. Fara episoade dureroase la efort.', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-16 10:55:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(61, '2026-06-18 10:55:00.000000', 'Sedinta 5 finalizata. Glezna stanga este perfect stabila, cu mobilitate simetrica fata de cea dreapta. Pacienta reintra in program normal de efort.', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-18 10:55:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),

-- Note de evolutie: Mihaela Neagu (Alexandru Dumitrescu)
(62, '2026-06-01 13:05:00.000000', 'Reluare program de recuperare. Am lucrat pe core-stability si tonifiere paravertebrala. Contractura musculara pe trapez este moderata.', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-01 13:05:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(63, '2026-06-03 11:55:00.000000', 'Stretching activ al umerilor si spatelui. Pacienta a raportat ca tensiunea din zona cefei a scazut in ultimele zile.', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-03 11:55:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(64, '2026-06-05 11:55:00.000000', 'Exercitii izometrice din sprijin pe coate. Pacienta demonstreaza control postural optim la oglinda.', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-05 11:55:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(65, '2026-06-10 11:55:00.000000', 'Am marit numarul de repetari la exercitiile posturale. Pacienta executa programul fara jena sau oboseala musculara acuta.', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-10 11:55:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(66, '2026-06-12 11:55:00.000000', 'Finalizare program de recuperare din Iunie. Postura globala este aliniata corect, durerea la birou este complet absenta.', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-12 11:55:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),

-- Note de evolutie: Mihai Stan (Alexandru Dumitrescu)
(67, '2026-06-02 14:55:00.000000', 'Reluare program post-lombosciatica. Mobilizari blande pe saltea si exercitii in descarcare de greutate. Durere declarata la flexia maxima de sold.', '5f89ace3-454d-486a-accd-986caa20d84a', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-02 14:55:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(68, '2026-06-04 14:55:00.000000', 'Am introdus exercitii de decompresie si stretching pe lantul posterior. Senzatia de iradiere pe piciorul stang s-a retras considerabil.', '5f89ace3-454d-486a-accd-986caa20d84a', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-04 14:55:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(69, '2026-06-09 14:55:00.000000', 'Lucru intens pe tonifiere abdominala profunda (stabilizare bazin). Mersul este corect si echilibrat.', '5f89ace3-454d-486a-accd-986caa20d84a', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-09 14:55:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(70, '2026-06-11 14:55:00.000000', 'Sedinta 4 finalizata. Control postural optim la rotatii. Pacientul se simte mult mai stapan pe miscarile dinamice.', '5f89ace3-454d-486a-accd-986caa20d84a', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-11 14:55:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', '05e0f10e-2c4d-405e-abac-5ccedb83d2af');
""";

# 4. New SQL for jurnale in pacienti_service
new_jurnale_sql = """
-- ==============================================================================
-- RICH MOCK DATA FOR PACIENTI JOURNAL (JUNE-JULY)
-- ==============================================================================
INSERT INTO `jurnal_pacient` (`id`, `comentarii`, `created_at`, `data`, `dificultate_exercitii`, `nivel_durere`, `nivel_oboseala`, `pacient_id`, `programare_id`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
-- Mihnea Matei (pacient_id = 21, keycloak: 00179244-a86e-4ade-8e22-3d70af6de57d)
(140, 'Am simțit o ușoară jenă cervicală la început, dar s-a ameliorat rapid cu încălzirea.', '2026-06-01 16:00:00.000000', '2026-06-01', 4, 2, 4, 21, 140, '2026-06-01 16:00:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '00179244-a86e-4ade-8e22-3d70af6de57d'),
(141, 'Exercițiile posturale au fost solicitante azi, spatele se simte stabil.', '2026-06-03 16:30:00.000000', '2026-06-03', 5, 1, 5, 21, 141, '2026-06-03 16:30:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '00179244-a86e-4ade-8e22-3d70af6de57d'),
(142, 'Ședință foarte bună, intensitate potrivită, nu am deloc dureri de cap.', '2026-06-05 17:15:00.000000', '2026-06-05', 3, 0, 3, 21, 142, '2026-06-05 17:15:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '00179244-a86e-4ade-8e22-3d70af6de57d'),
(143, 'Mă simt excelent. Am finalizat programul inițial, umerii mei sunt complet relaxați.', '2026-06-08 16:00:00.000000', '2026-06-08', 3, 0, 3, 21, 143, '2026-06-08 16:00:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '00179244-a86e-4ade-8e22-3d70af6de57d'),
(144, 'Reevaluare extrem de detaliată, am înțeles clar planul de menținere.', '2026-06-10 15:45:00.000000', '2026-06-10', 2, 0, 2, 21, 144, '2026-06-10 15:45:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '00179244-a86e-4ade-8e22-3d70af6de57d'),
(145, 'Început bun de program profilactic, stretchingul a fost foarte relaxant.', '2026-06-15 16:30:00.000000', '2026-06-15', 3, 0, 3, 21, 145, '2026-06-15 16:30:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '00179244-a86e-4ade-8e22-3d70af6de57d'),
(146, 'Control postural excelent, exercițiile de core au mers minunat.', '2026-06-17 16:15:00.000000', '2026-06-17', 3, 0, 2, 21, 146, '2026-06-17 16:15:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '00179244-a86e-4ade-8e22-3d70af6de57d'),
(147, 'Am terminat recuperarea! Mulțumesc frumos, mă simt ca nou.', '2026-06-19 16:00:00.000000', '2026-06-19', 2, 0, 2, 21, 147, '2026-06-19 16:00:00.000000', '00179244-a86e-4ade-8e22-3d70af6de57d', '00179244-a86e-4ade-8e22-3d70af6de57d'),

-- Irina Ionescu (pacient_id = 20, keycloak: b6f3d53a-f2d3-4e2b-b531-d1463a88ef70)
(150, 'Discuție detaliată despre reculul stabilității gleznei. Sunt motivată să lucrez.', '2026-06-02 11:00:00.000000', '2026-06-02', 3, 4, 3, 20, 150, '2026-06-02 11:00:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70'),
(151, 'Glezna se simte destul de rigidă la exerciții, dar durerea a fost suportabilă.', '2026-06-04 11:30:00.000000', '2026-06-04', 5, 3, 4, 20, 151, '2026-06-04 11:30:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70'),
(152, 'Placa de echilibru a fost solicitantă, dar mă simt mai sigură pe sprijin.', '2026-06-09 11:45:00.000000', '2026-06-09', 4, 2, 4, 20, 152, '2026-06-09 11:45:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70'),
(153, 'Exercițiile cu bandă elastică merg bine, stabilitatea este tot mai bună.', '2026-06-11 11:15:00.000000', '2026-06-11', 4, 1, 3, 20, 153, '2026-06-11 11:15:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70'),
(154, 'Pot merge 30 de minute fără nicio jena sau oboseală la nivelul gleznei.', '2026-06-16 11:00:00.000000', '2026-06-16', 3, 0, 3, 20, 154, '2026-06-16 11:00:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70'),
(155, 'Am finalizat cu succes calupul. Glezna mea este din nou stabilă și rezistentă la efort.', '2026-06-18 11:00:00.000000', '2026-06-18', 2, 0, 2, 20, 155, '2026-06-18 11:00:00.000000', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70'),

-- Mihaela Neagu (pacient_id = 14, keycloak: 31d03fc1-1800-4a52-9817-0c6b21d78e14)
(160, 'Mă bucur că am reluat kinetoterapia. Spatele este destul de tensionat după birou.', '2026-06-01 13:30:00.000000', '2026-06-01', 4, 3, 4, 14, 160, '2026-06-01 13:30:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '31d03fc1-1800-4a52-9817-0c6b21d78e14'),
(161, 'Tensiunea din zona umerilor a scăzut, exercițiile de core au fost intense.', '2026-06-03 12:15:00.000000', '2026-06-03', 4, 2, 3, 14, 161, '2026-06-03 12:15:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '31d03fc1-1800-4a52-9817-0c6b21d78e14'),
(162, 'Control motor foarte bun azi, spatele se simte bine consolidat.', '2026-06-05 12:00:00.000000', '2026-06-05', 3, 1, 3, 14, 162, '2026-06-05 12:00:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '31d03fc1-1800-4a52-9817-0c6b21d78e14'),
(163, 'Rezistență crescută la izometrie, postura mea s-a îmbunătățit vizibil.', '2026-06-10 12:00:00.000000', '2026-06-10', 3, 0, 3, 14, 163, '2026-06-10 12:00:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '31d03fc1-1800-4a52-9817-0c6b21d78e14'),
(164, 'Fără nicio durere la finalul programului, mă simt complet recuperată și energică.', '2026-06-12 12:00:00.000000', '2026-06-12', 2, 0, 2, 14, 164, '2026-06-12 12:00:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '31d03fc1-1800-4a52-9817-0c6b21d78e14'),

-- Mihai Stan (pacient_id = 10, keycloak: 5f89ace3-454d-486a-accd-986caa20d84a)
(170, 'Am simțit jena lombară în timpul exercițiilor pe saltea, dar masajul m-a ajutat.', '2026-06-02 15:00:00.000000', '2026-06-02', 4, 3, 4, 10, 170, '2026-06-02 15:00:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', '5f89ace3-454d-486a-accd-986caa20d84a'),
(171, 'Senzația de iradiere pe picior a dispărut complet după ședință. Progres bun.', '2026-06-04 15:30:00.000000', '2026-06-04', 4, 1, 3, 10, 171, '2026-06-04 15:30:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', '5f89ace3-454d-486a-accd-986caa20d84a'),
(172, 'Tonifierea zonei lombare merge excelent, pot sta la birou fără disconfort.', '2026-06-09 15:15:00.000000', '2026-06-09', 3, 0, 3, 10, 172, '2026-06-09 15:15:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', '5f89ace3-454d-486a-accd-986caa20d84a'),
(173, 'Fără durere, forța și mobilitatea spatelui au crescut substanțial.', '2026-06-11 15:00:00.000000', '2026-06-11', 2, 0, 2, 10, 173, '2026-06-11 15:00:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', '5f89ace3-454d-486a-accd-986caa20d84a');
""";

def inject_additional_data():
    # Write empty / fresh mock-data-june-july.sql first to ensure it's clean and doesn't double-append
    print(f"Initializing/cleaning {mock_data_file}...")
    with open(mock_data_file, "w", encoding="utf-8") as f:
        f.write("-- Mock data for June-July clinical cycles\n")

    print("Reading programari_service.sql...")
    with open(prog_file, "r", encoding="utf-8") as f:
        prog_content = f.read()
        
    # Append programari
    target_prog_end = "/*!40000 ALTER TABLE `programari` ENABLE KEYS */;"
    if target_prog_end in prog_content:
        if "Mihnea Matei (Keycloak: 00179244-a86e-4ade-8e22-3d70af6de57d" not in prog_content:
            prog_content = prog_content.replace(target_prog_end, new_programari_sql + target_prog_end, 1)
            print("Successfully injected new programari.")
        else:
            print("Programari already injected.")
            
    # Append evaluari (as complete independent statement)
    target_eval_end = "/*!40000 ALTER TABLE `evaluari` ENABLE KEYS */;"
    if target_eval_end in prog_content:
        if "EVALUARI CLINICE PENTRU PACIENTII LUI ALEXANDRU" not in prog_content:
            prog_content = prog_content.replace(target_eval_end, new_evaluari_sql + target_eval_end, 1)
            print("Successfully injected new evaluari.")
        else:
            print("Evaluari already injected.")
            
    # Append evolutii (as complete independent statement)
    target_evol_end = "/*!40000 ALTER TABLE `evolutii` ENABLE KEYS */;"
    if target_evol_end in prog_content:
        if "EVOLUTII CLINICE PENTRU PACIENTII LUI ALEXANDRU" not in prog_content:
            prog_content = prog_content.replace(target_evol_end, new_evolutii_sql + target_evol_end, 1)
            print("Successfully injected new evolutii.")
        else:
            print("Evolutii already injected.")
            
    with open(prog_file, "w", encoding="utf-8") as f:
        f.write(prog_content)
        
    # 2. Append pacienti jurnale
    print("Reading pacienti_service.sql...")
    with open(pac_file, "r", encoding="utf-8") as f:
        pac_content = f.read()
        
    target_pac_end = "/*!40000 ALTER TABLE `jurnal_pacient` ENABLE KEYS */;"
    if target_pac_end in pac_content:
        if "Mihnea Matei (pacient_id = 21" not in pac_content:
            pac_content = pac_content.replace(target_pac_end, new_jurnale_sql + target_pac_end, 1)
            print("Successfully injected new journals.")
        else:
            print("Journals already injected.")
            
    with open(pac_file, "w", encoding="utf-8") as f:
        f.write(pac_content)
        
    # 3. Also append to mock-data-june-july.sql just to keep it in sync and documented
    print("Writing to mock-data-june-july.sql...")
    append_mock_sql = f"""
-- ============================================================================
-- ADDITIONAL RICH CLINICAL DATA FOR ALEXANDRU DUMITRESCU'S PATIENTS (JUNE-JULY)
-- ============================================================================
USE `programari_service`;

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
