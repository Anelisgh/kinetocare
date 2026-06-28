-- Mock data for June-July clinical cycles

-- ============================================================================
-- ADDITIONAL RICH CLINICAL DATA FOR ALEXANDRU DUMITRESCU'S PATIENTS (JUNE-JULY)
-- ============================================================================
USE `programari_service`;

-- Programari

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


-- Evaluari

-- ==============================================================================
-- EVALUARI CLINICE PENTRU PACIENTII LUI ALEXANDRU DUMITRESCU (JUNE-JULY)
-- ==============================================================================
INSERT INTO `evaluari` (`id`, `created_at`, `data`, `diagnostic`, `observatii`, `pacient_keycloak_id`, `programare_id`, `sedinte_recomandate`, `serviciu_recomandat_id`, `terapeut_keycloak_id`, `tip`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
-- Mihnea Matei reevaluare
(26, '2026-06-10 15:35:00.000000', '2026-06-10', 'Cervicalgie miotensivă - Ameliorată', 'Control postural optim. Forta paravertebrala cervicala refacuta complet. Pacientul incepe planul profilactic de mentinere (3 sedinte recomandate).', '00179244-a86e-4ade-8e22-3d70af6de57d', 144, 3, 8, '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'REEVALUARE', '2026-06-10 15:35:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
-- Irina Ionescu reevaluare (pauza din martie)
(27, '2026-06-02 10:35:00.000000', '2026-06-02', 'Entorsă gleznă grad II - Mobilitate redusă', 'Stabilitatea gleznei a scazut in perioada de inactivitate. Mobilitatea in flexie dorsala este limitata si insotita de jena la 15 grade. Se recomanda 5 sedinte de kinetoterapie pentru proprioceptie.', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 150, 5, 6, '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'REEVALUARE', '2026-06-02 10:35:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af');


-- Evolutii

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


USE `pacienti_service`;

-- Jurnale

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


-- ============================================================================
-- ADDITIONAL RICH CLINICAL MOCK DATA FOR ALL REMAINING PATIENTS (JUNE)
-- ============================================================================
USE `programari_service`;

-- Relatii

-- ==============================================================================
-- NOI RELATII ACTIVE PACIENT-TERAPEUT (JUNE)
-- ==============================================================================
INSERT INTO `relatie_pacient_terapeut` (`id`, `activa`, `created_at`, `data_inceput`, `data_sfarsit`, `pacient_keycloak_id`, `terapeut_keycloak_id`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
(30, b'1', '2026-06-01 09:00:00.000000', '2026-06-01', NULL, '9f90d398-f030-4ce4-bfee-033758993378', '496b7af4-d378-418d-8d5f-5647ed31d11d', '2026-06-01 09:00:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d');


-- Programari

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


-- Evaluari

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


-- Evolutii

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


USE `pacienti_service`;

-- Jurnale

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



-- ==============================================================================
-- FINAL CLINICAL & CHRONOLOGICAL CORRECTIONS (JUNE-JULY)
-- ==============================================================================
USE programari_service;

-- 1. Neagu Mihaela: Jul 1 session (id=165) -> REEVALUARE
UPDATE programari SET serviciu_id=2, tip_serviciu='Evaluare - Reevaluare', are_evaluare=1, durata_minute=30, ora_sfarsit='11:30:00', pret=150.00 WHERE id=165;

-- 2. Ionescu Irina: Jul 7 session (id=156) -> REEVALUARE
UPDATE programari SET serviciu_id=2, tip_serviciu='Evaluare - Reevaluare', are_evaluare=1, durata_minute=30, ora_sfarsit='10:30:00', pret=150.00 WHERE id=156;
-- Fix her Feb 16 evaluation cota to 10
UPDATE evaluari SET sedinte_recomandate=10 WHERE pacient_keycloak_id='b6f3d53a-f2d3-4e2b-b531-d1463a88ef70' AND data='2026-02-16';

-- 3. Mihnea Matei: Jul 1 session (id=148) -> REEVALUARE
UPDATE programari SET serviciu_id=2, tip_serviciu='Evaluare - Reevaluare', are_evaluare=1, durata_minute=30, ora_sfarsit='15:30:00', pret=150.00 WHERE id=148;
-- Fix his March 25 reevaluation cota to 4
UPDATE evaluari SET sedinte_recomandate=4 WHERE pacient_keycloak_id='00179244-a86e-4ade-8e22-3d70af6de57d' AND data='2026-03-25';

-- 4. Tudor Robert: Keep cota as 8 and add July 3 Reevaluare
UPDATE evaluari SET sedinte_recomandate=8 WHERE pacient_keycloak_id='6da1727a-5388-4bfb-b845-d96485c61498' AND data='2026-03-12';
-- Insert July 3 Reevaluare (id=500)
INSERT IGNORE INTO programari (id, are_evaluare, are_jurnal, created_at, data, durata_minute, locatie_id, motiv_anulare, ora_inceput, ora_sfarsit, pacient_keycloak_id, pret, prima_intalnire, serviciu_id, status, terapeut_keycloak_id, tip_serviciu, updated_at, created_by, last_modified_by) VALUES
(500, 1, 0, '2026-06-05 09:00:00.000000', '2026-07-03', 30, 13, NULL, '09:00:00', '09:30:00', '6da1727a-5388-4bfb-b845-d96485c61498', 150.00, 0, 2, 'PROGRAMATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Evaluare - Reevaluare', '2026-06-05 09:00:00.000000', 'SYSTEM', 'SYSTEM');

-- 5. Dobre Daniel: Future sessions -> PROGRAMATA
UPDATE programari SET status='PROGRAMATA' WHERE id IN (384, 385);

-- 6. Dumitrescu Ana: Future sessions -> PROGRAMATA
UPDATE programari SET status='PROGRAMATA' WHERE id IN (395, 396, 397);

-- 7. Ene Sabrina: Future sessions -> PROGRAMATA
UPDATE programari SET status='PROGRAMATA' WHERE id IN (108, 109, 110, 112);

-- 8. Freya Gh: Add July 6 Reevaluare (id=501)
INSERT IGNORE INTO programari (id, are_evaluare, are_jurnal, created_at, data, durata_minute, locatie_id, motiv_anulare, ora_inceput, ora_sfarsit, pacient_keycloak_id, pret, prima_intalnire, serviciu_id, status, terapeut_keycloak_id, tip_serviciu, updated_at, created_by, last_modified_by) VALUES
(501, 1, 0, '2026-06-15 10:00:00.000000', '2026-07-06', 30, 1, NULL, '10:00:00', '10:30:00', '2733282b-1ee8-4afb-827a-aa0ff0b98f61', 150.00, 0, 2, 'PROGRAMATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Evaluare - Reevaluare', '2026-06-15 10:00:00.000000', 'SYSTEM', 'SYSTEM');

-- 9. Gheorghe Elena: Add July 7 Reevaluare (id=502)
INSERT IGNORE INTO programari (id, are_evaluare, are_jurnal, created_at, data, durata_minute, locatie_id, motiv_anulare, ora_inceput, ora_sfarsit, pacient_keycloak_id, pret, prima_intalnire, serviciu_id, status, terapeut_keycloak_id, tip_serviciu, updated_at, created_by, last_modified_by) VALUES
(502, 1, 0, '2026-06-16 11:10:00.000000', '2026-07-07', 30, 2, NULL, '11:10:00', '11:40:00', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 150.00, 0, 2, 'PROGRAMATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Evaluare - Reevaluare', '2026-06-16 11:10:00.000000', 'SYSTEM', 'SYSTEM');

-- 10. Ionescu Maria: Future sessions -> PROGRAMATA
UPDATE programari SET status='PROGRAMATA' WHERE id IN (121, 122, 123, 124, 420);

-- 11. Marin Cristian: Add July 6 Reevaluare (id=503)
INSERT IGNORE INTO programari (id, are_evaluare, are_jurnal, created_at, data, durata_minute, locatie_id, motiv_anulare, ora_inceput, ora_sfarsit, pacient_keycloak_id, pret, prima_intalnire, serviciu_id, status, terapeut_keycloak_id, tip_serviciu, updated_at, created_by, last_modified_by) VALUES
(503, 1, 0, '2026-06-15 13:00:00.000000', '2026-07-06', 30, 10, NULL, '13:00:00', '13:30:00', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 150.00, 0, 2, 'PROGRAMATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Evaluare - Reevaluare', '2026-06-15 13:00:00.000000', 'SYSTEM', 'SYSTEM');

-- 12. Pavel Oana: Future sessions -> PROGRAMATA
UPDATE programari SET status='PROGRAMATA' WHERE id IN (133, 134, 135, 136);

-- 13. Popescu Alina: Move 4th/5th to future & PROGRAMATA
UPDATE programari SET data='2026-06-26', status='PROGRAMATA' WHERE id=204;
UPDATE programari SET data='2026-06-29', status='PROGRAMATA' WHERE id=205;

-- 14. Popescu Andrei: Move 5th to future & PROGRAMATA
UPDATE programari SET data='2026-06-26', status='PROGRAMATA' WHERE id=208;

-- 15. Popescu Ana-Maria: Future sessions -> PROGRAMATA
UPDATE programari SET status='PROGRAMATA' WHERE id=414;
UPDATE programari SET data='2026-06-29', status='PROGRAMATA' WHERE id=415;

-- 16. Sorin Tudor: Move 6th to future & PROGRAMATA
UPDATE programari SET data='2026-06-26', status='PROGRAMATA' WHERE id=226;

-- 17. Stan Mihai: Jul 2 session (id=174) -> REEVALUARE
UPDATE programari SET serviciu_id=2, tip_serviciu='Evaluare - Reevaluare', are_evaluare=1, durata_minute=30, ora_sfarsit='14:30:00', pret=150.00 WHERE id=174;

-- 18. Stan Daniela: Move 6th to future & PROGRAMATA
UPDATE programari SET data='2026-06-29', status='PROGRAMATA' WHERE id=403;


-- ==============================================================================
-- DELETE JOURNALS FOR FUTURE/PROGRAMATA APPOINTMENTS (JUNE-JULY)
-- ==============================================================================
USE pacienti_service;

DELETE FROM jurnal_pacient WHERE programare_id IN (204, 205, 208, 414, 415, 226, 403);


-- ==========================================
-- USER CLINICAL LOGIC & DE-DUPLICATION CORRECTIONS
-- ==========================================
USE pacienti_service;

-- USER CORRECTIONS - June 25, 2026
-- Delete orphaned journals referencing deleted future appointments
DELETE FROM jurnal_pacient WHERE programare_id IN (109, 110, 112, 122, 123, 124, 134, 135, 136);

USE programari_service;

-- USER CORRECTIONS - June 25, 2026
-- 1. Popescu Ana-Maria: Delete duplicate March 30 Initial Evaluation (App 64)
DELETE FROM programari WHERE id = 64;

-- 2. Dumitrescu Ana: Delete duplicate March 30 Initial Evaluation (App 16)
DELETE FROM programari WHERE id = 16;

-- 3. Popescu Ana-Maria: Correct therapist relationships
-- Clara Maria (496b7af4-d378-418d-8d5f-5647ed31d11d) should be active, Alexandru Dumitrescu (05e0f10e-2c4d-405e-abac-5ccedb83d2af) should be inactive starting June 1st.
UPDATE relatie_pacient_terapeut SET activa = 1, data_sfarsit = NULL WHERE id = 30;
UPDATE relatie_pacient_terapeut SET activa = 0, data_sfarsit = '2026-06-01' WHERE id = 31;

-- 4. Limit future scheduled appointments (only one 'PROGRAMATA' in the future per patient)
-- Pavel Oana: Keep App 133, delete others
DELETE FROM programari WHERE id IN (134, 135, 136);

-- Ionescu Maria: Keep App 121, delete others
DELETE FROM programari WHERE id IN (122, 123, 124, 420);

-- Ene Sabrina: Keep App 108, delete others
DELETE FROM programari WHERE id IN (109, 110, 112);

-- Mihnea Matei: Keep App 148, delete others
DELETE FROM programari WHERE id = 149;


-- ==========================================
-- ADDITIONAL USER CLINICAL TIMELINE UPDATES
-- ==========================================
USE pacienti_service;

-- ADDITIONAL USER FIXES - June 25, 2026

-- Delete journals for any deleted/modified future appointments if any exist
DELETE FROM jurnal_pacient WHERE programare_id IN (612, 621, 631);

-- Insert new past journals for Neagu Mihaela
INSERT INTO jurnal_pacient (comentarii, created_at, data, dificultate_exercitii, nivel_durere, nivel_oboseala, pacient_id, programare_id, updated_at, created_by, last_modified_by) VALUES
('Mă simt mult mai mobil după această reevaluare. Rămân dedicat planului stabilit.', '2026-06-15 13:35:00.000000', '2026-06-15', 3, 3, 4, 14, 165, '2026-06-15 13:35:00.000000', 'SYSTEM', 'SYSTEM'),
('Exercițiile au fost obositoare dar mă simt stabil.', '2026-06-18 13:55:00.000000', '2026-06-18', 3, 2, 3, 14, 610, '2026-06-18 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
('Spatele se simte din ce în ce mai bine după ședințe.', '2026-06-22 13:55:00.000000', '2026-06-22', 3, 2, 2, 14, 611, '2026-06-22 13:55:00.000000', 'SYSTEM', 'SYSTEM');

-- Insert new past journals for Ionescu Irina
INSERT INTO jurnal_pacient (comentarii, created_at, data, dificultate_exercitii, nivel_durere, nivel_oboseala, pacient_id, programare_id, updated_at, created_by, last_modified_by) VALUES
('Reevaluarea a arătat un progres bun. Sunt încurajată să continui.', '2026-06-22 10:35:00.000000', '2026-06-22', 2, 3, 3, 20, 156, '2026-06-22 10:35:00.000000', 'SYSTEM', 'SYSTEM'),
('Prima ședință din noul pachet a mers bine.', '2026-06-24 11:00:00.000000', '2026-06-24', 3, 2, 3, 20, 620, '2026-06-24 11:00:00.000000', 'SYSTEM', 'SYSTEM');

-- Insert new past journals for Mihnea Matei
INSERT INTO jurnal_pacient (comentarii, created_at, data, dificultate_exercitii, nivel_durere, nivel_oboseala, pacient_id, programare_id, updated_at, created_by, last_modified_by) VALUES
('Starea fizică este excelentă. Reevaluarea a arătat rezultate bune.', '2026-06-22 09:35:00.000000', '2026-06-22', 2, 1, 2, 21, 148, '2026-06-22 09:35:00.000000', 'SYSTEM', 'SYSTEM'),
('O ședință excelentă de gimnastică posturală.', '2026-06-24 10:00:00.000000', '2026-06-24', 2, 1, 2, 21, 630, '2026-06-24 10:00:00.000000', 'SYSTEM', 'SYSTEM');

USE programari_service;

-- ADDITIONAL USER FIXES - June 25, 2026

-- 1. Stan Daniela: Delete duplicate March 27 Initial Evaluation (App 26)
DELETE FROM programari WHERE id = 26;

-- 2. Neagu Mihaela: Change App 165 from scheduled to completed Reevaluare on June 15
UPDATE programari SET data = '2026-06-15', ora_inceput = '13:00:00', ora_sfarsit = '13:30:00', status = 'FINALIZATA', are_jurnal = b'1' WHERE id = 165;

-- Insert new evaluation for Neagu Mihaela on June 15
INSERT INTO evaluari (created_at, data, diagnostic, observatii, pacient_keycloak_id, programare_id, sedinte_recomandate, serviciu_recomandat_id, terapeut_keycloak_id, tip, updated_at, created_by, last_modified_by) VALUES
('2026-06-15 13:30:00.000000', '2026-06-15', 'Lombosciatică stângă', 'Stare ameliorată, mobilitate crescută la nivel lombar. Se continuă planul terapeutic cu încă 6 ședințe de tonifiere musculară profundă.', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 165, 6, 6, '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'REEVALUARE', '2026-06-15 13:30:00.000000', 'SYSTEM', 'SYSTEM');

-- Add new past sessions for Neagu Mihaela (App 610, 611)
INSERT INTO programari (id, are_evaluare, are_jurnal, created_at, data, durata_minute, locatie_id, motiv_anulare, ora_inceput, ora_sfarsit, pacient_keycloak_id, pret, prima_intalnire, serviciu_id, status, terapeut_keycloak_id, tip_serviciu, updated_at, created_by, last_modified_by) VALUES
(610, b'0', b'1', '2026-06-15 13:40:00.000000', '2026-06-18', 50, 1, NULL, '13:00:00', '13:50:00', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-18 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(611, b'0', b'1', '2026-06-15 13:45:00.000000', '2026-06-22', 50, 1, NULL, '13:00:00', '13:50:00', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-22 13:55:00.000000', 'SYSTEM', 'SYSTEM');

-- Add progress notes (evolutii) for Neagu Mihaela
INSERT INTO evolutii (created_at, observatii, pacient_keycloak_id, terapeut_keycloak_id, updated_at, created_by, last_modified_by) VALUES
('2026-06-18 13:55:00.000000', 'Tonifiere musculară generală. Pacientul răspunde bine la exercițiile de stabilizare lombară. Toleranță crescută la efort.', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-18 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
('2026-06-22 13:55:00.000000', 'S-au efectuat exerciții terapeutice cu rezistență medie. Tonus muscular bun. Durerea lombară a scăzut semnificativ.', '31d03fc1-1800-4a52-9817-0c6b21d78e14', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-22 13:55:00.000000', 'SYSTEM', 'SYSTEM');

-- Add 1 future scheduled session for Neagu Mihaela (App 612)
INSERT INTO programari (id, are_evaluare, are_jurnal, created_at, data, durata_minute, locatie_id, motiv_anulare, ora_inceput, ora_sfarsit, pacient_keycloak_id, pret, prima_intalnire, serviciu_id, status, terapeut_keycloak_id, tip_serviciu, updated_at, created_by, last_modified_by) VALUES
(612, b'0', b'0', '2026-06-15 13:50:00.000000', '2026-07-01', 50, 1, NULL, '13:00:00', '13:50:00', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'PROGRAMATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-15 13:50:00.000000', 'SYSTEM', 'SYSTEM');


-- 3. Ionescu Irina: Change App 156 from scheduled to completed Reevaluare on June 22
UPDATE programari SET data = '2026-06-22', status = 'FINALIZATA', are_jurnal = b'1' WHERE id = 156;

-- Insert new evaluation for Ionescu Irina on June 22
INSERT INTO evaluari (created_at, data, diagnostic, observatii, pacient_keycloak_id, programare_id, sedinte_recomandate, serviciu_recomandat_id, terapeut_keycloak_id, tip, updated_at, created_by, last_modified_by) VALUES
('2026-06-22 10:30:00.000000', '2026-06-22', 'Hernie de disc L5-S1', 'Evoluție favorabilă. Se constată creșterea mobilității articulare. Se recomandă continuarea cu un nou set de 5 ședințe pentru consolidarea rezultatelor.', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 156, 5, 6, '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'REEVALUARE', '2026-06-22 10:30:00.000000', 'SYSTEM', 'SYSTEM');

-- Add new past session for Ionescu Irina (App 620)
INSERT INTO programari (id, are_evaluare, are_jurnal, created_at, data, durata_minute, locatie_id, motiv_anulare, ora_inceput, ora_sfarsit, pacient_keycloak_id, pret, prima_intalnire, serviciu_id, status, terapeut_keycloak_id, tip_serviciu, updated_at, created_by, last_modified_by) VALUES
(620, b'0', b'1', '2026-06-22 10:40:00.000000', '2026-06-24', 50, 1, NULL, '10:00:00', '10:50:00', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-24 11:00:00.000000', 'SYSTEM', 'SYSTEM');

-- Add progress note (evolutie) for Ionescu Irina
INSERT INTO evolutii (created_at, observatii, pacient_keycloak_id, terapeut_keycloak_id, updated_at, created_by, last_modified_by) VALUES
('2026-06-24 11:00:00.000000', 'Exerciții de posturare globală. Corecție activă a coloanei lombare. Toleranță musculară crescută la posturile prelungite.', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-24 11:00:00.000000', 'SYSTEM', 'SYSTEM');

-- Add 1 future scheduled session for Ionescu Irina (App 621)
INSERT INTO programari (id, are_evaluare, are_jurnal, created_at, data, durata_minute, locatie_id, motiv_anulare, ora_inceput, ora_sfarsit, pacient_keycloak_id, pret, prima_intalnire, serviciu_id, status, terapeut_keycloak_id, tip_serviciu, updated_at, created_by, last_modified_by) VALUES
(621, b'0', b'0', '2026-06-22 10:50:00.000000', '2026-07-07', 50, 1, NULL, '10:00:00', '10:50:00', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 100.00, b'0', 6, 'PROGRAMATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-06-22 10:50:00.000000', 'SYSTEM', 'SYSTEM');


-- 4. Mihnea Matei: Change App 148 from scheduled to completed Reevaluare on June 22
UPDATE programari SET data = '2026-06-22', status = 'FINALIZATA', are_jurnal = b'1' WHERE id = 148;

-- Insert new evaluation for Mihnea Matei on June 22
INSERT INTO evaluari (created_at, data, diagnostic, observatii, pacient_keycloak_id, programare_id, sedinte_recomandate, serviciu_recomandat_id, terapeut_keycloak_id, tip, updated_at, created_by, last_modified_by) VALUES
('2026-06-22 09:30:00.000000', '2026-06-22', 'Sindrom postural cronic', 'Progres semnificativ. Aliniament postural optim în poziție statică. Se recomandă încă 4 ședințe de gimnastică profilactică pentru menținerea tonusului și a posturii corecte.', '00179244-a86e-4ade-8e22-3d70af6de57d', 148, 4, 8, '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'REEVALUARE', '2026-06-22 09:30:00.000000', 'SYSTEM', 'SYSTEM');

-- Add new past session for Mihnea Matei (App 630)
INSERT INTO programari (id, are_evaluare, are_jurnal, created_at, data, durata_minute, locatie_id, motiv_anulare, ora_inceput, ora_sfarsit, pacient_keycloak_id, pret, prima_intalnire, serviciu_id, status, terapeut_keycloak_id, tip_serviciu, updated_at, created_by, last_modified_by) VALUES
(630, b'0', b'1', '2026-06-22 09:40:00.000000', '2026-06-24', 50, 1, NULL, '09:00:00', '09:50:00', '00179244-a86e-4ade-8e22-3d70af6de57d', 80.00, b'0', 8, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Gimnastică profilactică', '2026-06-24 10:00:00.000000', 'SYSTEM', 'SYSTEM');

-- Add progress note (evolutie) for Mihnea Matei
INSERT INTO evolutii (created_at, observatii, pacient_keycloak_id, terapeut_keycloak_id, updated_at, created_by, last_modified_by) VALUES
('2026-06-24 10:00:00.000000', 'Gimnastică posturală controlată. Mobilitate excelentă a coloanei cervicale și toracice. Fără semne de oboseală musculară.', '00179244-a86e-4ade-8e22-3d70af6de57d', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '2026-06-24 10:00:00.000000', 'SYSTEM', 'SYSTEM');

-- Add 1 future scheduled session for Mihnea Matei (App 631)
INSERT INTO programari (id, are_evaluare, are_jurnal, created_at, data, durata_minute, locatie_id, motiv_anulare, ora_inceput, ora_sfarsit, pacient_keycloak_id, pret, prima_intalnire, serviciu_id, status, terapeut_keycloak_id, tip_serviciu, updated_at, created_by, last_modified_by) VALUES
(631, b'0', b'0', '2026-06-22 09:50:00.000000', '2026-07-01', 50, 1, NULL, '09:00:00', '09:50:00', '00179244-a86e-4ade-8e22-3d70af6de57d', 80.00, b'0', 8, 'PROGRAMATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Gimnastică profilactică', '2026-06-22 09:50:00.000000', 'SYSTEM', 'SYSTEM');


USE programari_service;

-- USER CORRECTIONS - June 25, 2026
-- Delete canceled appointment 63 to remove Popescu Ana-Maria from Alexandru's recent patients list
DELETE FROM programari WHERE id = 63;

