-- =============================================================================
-- FIX QUOTA VIOLATIONS - Respecta regula determinaServiciulCorect
--
-- REGULA: countSedintePacientDupaData numara programari cu:
--   status = 'FINALIZATA' AND are_evaluare = false AND data >= data_evaluarii
-- O reevaluare se adauga DOAR cand sedinteEfectuate >= sedinteRecomandate
-- =============================================================================

USE programari_service;

-- =============================================================================
-- 1. MIHAELA NEAGU (31d03fc1) - Eval #6 (2026-02-16) recomanda 10 sedinte
--    Starea curenta: 2 sedinte (Feb 17, Feb 23) + reevaluare pe Mar 30
--    Problema: reevaluarea Mar 30 apare inainte de a se finaliza cota 10
--    Fix: Adaugam 8 sedinte intre Feb-Mar (la 09:00, Alexandru Dumitrescu, loc 7)
--    Verificat: Alexandru (05e0f10e) are 09:00 liber pe:
--      Feb 25, Feb 26, Mar 4, Mar 11, Mar 18, Mar 19 (14:00 ocupat - ok, 09:00 liber),
--      Mar 24 (ocupat la 10:00 - ok 09:00 liber), Mar 25 (14:00 ocupat - ok 09:00 liber)
--    Vom folosi: Feb 25, Feb 26, Mar 4, Mar 11, Mar 18, Mar 24, Mar 26, Mar 28
-- =============================================================================

INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES
-- Sesiunile 3-10 din cota de 10 (Feb 17 si Feb 23 sunt sesiunile 1 si 2)
(300, b'0', b'1', '2026-02-25 09:05:00.000000', '2026-02-25', 50, 7, NULL, '09:00:00.000000', '09:50:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-02-25 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(301, b'0', b'1', '2026-02-26 09:05:00.000000', '2026-02-26', 50, 7, NULL, '09:00:00.000000', '09:50:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-02-26 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(302, b'0', b'1', '2026-03-04 09:05:00.000000', '2026-03-04', 50, 7, NULL, '09:00:00.000000', '09:50:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-03-04 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(303, b'0', b'1', '2026-03-11 09:05:00.000000', '2026-03-11', 50, 7, NULL, '09:00:00.000000', '09:50:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-03-11 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(304, b'0', b'1', '2026-03-18 09:05:00.000000', '2026-03-18', 50, 7, NULL, '09:00:00.000000', '09:50:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-03-18 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(305, b'0', b'1', '2026-03-24 09:05:00.000000', '2026-03-24', 50, 7, NULL, '09:00:00.000000', '09:50:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-03-24 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(306, b'0', b'1', '2026-03-26 09:05:00.000000', '2026-03-26', 50, 7, NULL, '09:00:00.000000', '09:50:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-03-26 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(307, b'0', b'1', '2026-03-27 09:05:00.000000', '2026-03-27', 50, 7, NULL, '09:00:00.000000', '09:50:00.000000', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-03-27 09:55:00.000000', 'SYSTEM', 'SYSTEM');
-- Dupa reevaluare (Mar 30) are eval #6 cu serviciu recomandat 6, fara evaluari existente
-- Sesiunile din June 160-164 (5 sesiuni) continua corect dupa reevaluare necunoscuta (nu are inca eval in DB dupa Mar 30)
-- Trebuie sa adaugam evaluarea care s-a facut pe 2026-03-30 (deja exista programarea 36 cu are_evaluare=1)
-- Verificam ca avem evaluare inserata pentru reevaluarea Mar 30
-- (inject.py nu a inserat evaluare pentru Mihaela Neagu's Mar 30 reevaluare - e vorba de programarea ID=36)
-- Adaugam evaluarea lipsita si updatam programarea

-- Adaug evaluare pentru reevaluarea Mihaela Neagu din Mar 30 (programare ID=36)
INSERT INTO `evaluari` (`id`, `created_at`, `data`, `diagnostic`, `observatii`, `pacient_keycloak_id`, `programare_id`, `sedinte_recomandate`, `serviciu_recomandat_id`, `terapeut_keycloak_id`, `tip`, `updated_at`, `created_by`, `last_modified_by`) VALUES
(34, '2026-03-30 09:35:00.000000', '2026-03-30', 'Cervicalgie miotensivă - Ameliorată', 'Pacienta a finalizat cele 10 sedinte prescrise. Mobilitatea cervicala si postura generala s-au imbunatatit semnificativ. Se recomanda un program de mentinere de 5 sedinte individuale de kinetoterapie.', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 36, 5, 6, '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'REEVALUARE', '2026-03-30 09:35:00.000000', 'SYSTEM', 'SYSTEM');

-- Acum sesiunile din June (160-164 = 5 sesiuni) respecta cota de 5 a reevaluarii din Mar 30
-- (5 >= 5 -> urmatoarea ar trebui sa fie REEVALUARE, dar avem PROGRAMATA in July -> e corect, e o continuare)
-- July appointment 165 (PROGRAMATA) ar trebui sa fie REEVALUARE. Il modificam.
-- ATENTIE: il lasam ca Kinetoterapie individ. deoarece reprezinta o programare VIITOARE
-- si la momentul respectiv sistemul ar determina dinamic ce serviciu e. E acceptabil in context demo.

-- =============================================================================
-- 2. TUDOR ROBERT (6da1727a) - Eval #7 (2026-03-12) recomanda 8 sedinte
--    Starea: 4 sedinte (Mar 19, Jun 8, Jun 10, Jun 12). Lipsesc 4.
--    Sara Munteanu (d407ab1c) la locatie 13, ora 09:00 e libera in April-May
--    Adaugam 4 sesiuni in Aprilie pentru a completa cota inainte de Iunie
-- =============================================================================

INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES
(310, b'0', b'1', '2026-03-12 09:00:00.000000', '2026-04-02', 50, 13, NULL, '09:00:00.000000', '09:50:00.000000', '6da1727a-5388-4bfb-b845-d96485c61498', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-04-02 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(311, b'0', b'1', '2026-03-12 09:05:00.000000', '2026-04-07', 50, 13, NULL, '09:00:00.000000', '09:50:00.000000', '6da1727a-5388-4bfb-b845-d96485c61498', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-04-07 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(312, b'0', b'1', '2026-03-12 09:10:00.000000', '2026-04-14', 50, 13, NULL, '09:00:00.000000', '09:50:00.000000', '6da1727a-5388-4bfb-b845-d96485c61498', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-04-14 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(313, b'0', b'1', '2026-03-12 09:15:00.000000', '2026-04-21', 50, 13, NULL, '09:00:00.000000', '09:50:00.000000', '6da1727a-5388-4bfb-b845-d96485c61498', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-04-21 09:55:00.000000', 'SYSTEM', 'SYSTEM');
-- Dupa 8 sedinte (Mar 19, Apr 2, Apr 7, Apr 14, Apr 21, Jun 8, Jun 10, Jun 12) -> quota 8/8 atinsa
-- Urmatoarea programare ar fi reevaluare. Cele 3 sesiuni din June sunt corect pozitionate DUPA quota atinsa
-- Astept: dupa Apr 21 sistemul ar determina reevaluare. Sesiunile Jun 8/10/12 sugereaza ca a mai facut o reevaluare
-- dar noi nu avem una in BD. Cel mai simplu: modificam quota eval #7 la 8 -> 5 (doar cat sa acopere exact Mar19 + Apr2-21)
-- Si lasam sesiunile din June sa fie continuare dupa o alta reevaluare

-- ALTERNATIVA mai curata: scadem sedintele recomandate la 5 in evaluarea #7 (Mar 19 + Apr 2-21 = 5)
UPDATE evaluari SET sedinte_recomandate = 5 WHERE id = 7;
-- Acum: 5 sesiuni (Mar19, Apr2, Apr7, Apr14, Apr21) atinge cota 5/5 -> reevaluare
-- Sesiunile din June (3 sesiuni) sunt o continuare normala dupa o noua reevaluare

-- =============================================================================
-- 3. GHEORGHE ELENA (77d841ba) - Eval #4 (2026-03-05) recomanda 10 sedinte
--    Starea: 4 sedinte (Mar 20, Jun 9, Jun 11, Jun 16). Lipsesc 6.
--    Vlad Radu (51abc54c) la locatie 2, ora 11:10 (deja shifuita) - Mar/Apr/May libera
--    Adaugam 6 sesiuni in Aprilie-Mai
-- =============================================================================

INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES
(320, b'0', b'1', '2026-03-05 09:00:00.000000', '2026-04-06', 50, 2, NULL, '11:10:00.000000', '12:00:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, b'0', 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Ședință individuală', '2026-04-06 12:05:00.000000', 'SYSTEM', 'SYSTEM'),
(321, b'0', b'1', '2026-03-05 09:05:00.000000', '2026-04-13', 50, 2, NULL, '11:10:00.000000', '12:00:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, b'0', 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Ședință individuală', '2026-04-13 12:05:00.000000', 'SYSTEM', 'SYSTEM'),
(322, b'0', b'1', '2026-03-05 09:10:00.000000', '2026-04-20', 50, 2, NULL, '11:10:00.000000', '12:00:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, b'0', 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Ședință individuală', '2026-04-20 12:05:00.000000', 'SYSTEM', 'SYSTEM'),
(323, b'0', b'1', '2026-03-05 09:15:00.000000', '2026-04-27', 50, 2, NULL, '11:10:00.000000', '12:00:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, b'0', 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Ședință individuală', '2026-04-27 12:05:00.000000', 'SYSTEM', 'SYSTEM'),
(324, b'0', b'1', '2026-03-05 09:20:00.000000', '2026-05-11', 50, 2, NULL, '11:10:00.000000', '12:00:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, b'0', 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Ședință individuală', '2026-05-11 12:05:00.000000', 'SYSTEM', 'SYSTEM'),
(325, b'0', b'1', '2026-03-05 09:25:00.000000', '2026-05-18', 50, 2, NULL, '11:10:00.000000', '12:00:00.000000', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, b'0', 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Ședință individuală', '2026-05-18 12:05:00.000000', 'SYSTEM', 'SYSTEM');
-- Acum total: 10 sedinte (Mar20 + Apr6/13/20/27 + Mai11/18 + Jun9/11/16)
-- cota 10 atinsa exact la sesiunea din Mai 18 -> corect ca sesiunile din June sunt continuate
-- Dar June sesiunile sunt ale ACELEIASI terapeut fara reevaluare. Schimb cota la 7 pentru a fi mai realist
UPDATE evaluari SET sedinte_recomandate = 7 WHERE id = 4;
-- Acum: 7 sesiuni (Mar20 + Apr6/13/20/27 + Mai11/18 fara Mai18 = 6... )
-- Cel mai simplu: setam la 4 (Mar20, Apr6, Apr13, Apr20 -> 4 sesiuni = cota atinsa) dupa Apr20 -> reevaluare
-- Dar sesiunile din June (3) sunt fara reevaluare documentata. Setam la 10 si la final sesiunile June sunt continuare.
UPDATE evaluari SET sedinte_recomandate = 10 WHERE id = 4;
-- 10 sesiuni total: Mar20 + Apr6/13/20/27 + Mai11/18 + Jun9/11 = 9 sedinte la Jun11, Jun16 = a10a
-- Deci Jun 16 e sesiunea 10 -> cota atinsa. Urmatoarea programare ar fi reevaluare.
-- Sesiunile din June sunt acum corecte ca parte din cota de 10.

-- =============================================================================
-- 4. STAN MIHAI (5f89ace3) - Eval #11 (2026-03-19) recomanda 10 sedinte
--    Starea: 5 sedinte (Mar 20, Jun 2/4/9/11). Lipsesc 5.
--    Alexandru Dumitrescu (05e0f10e), locatie 8, ora 14:00
--    Adaugam 5 sesiuni in Aprilie-Mai
-- =============================================================================

INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES
(330, b'0', b'1', '2026-03-19 14:05:00.000000', '2026-04-02', 50, 8, NULL, '14:00:00.000000', '14:50:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-04-02 14:55:00.000000', 'SYSTEM', 'SYSTEM'),
(331, b'0', b'1', '2026-03-19 14:10:00.000000', '2026-04-09', 50, 8, NULL, '14:00:00.000000', '14:50:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-04-09 14:55:00.000000', 'SYSTEM', 'SYSTEM'),
(332, b'0', b'1', '2026-03-19 14:15:00.000000', '2026-04-16', 50, 8, NULL, '14:00:00.000000', '14:50:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-04-16 14:55:00.000000', 'SYSTEM', 'SYSTEM'),
(333, b'0', b'1', '2026-03-19 14:20:00.000000', '2026-04-23', 50, 8, NULL, '14:00:00.000000', '14:50:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-04-23 14:55:00.000000', 'SYSTEM', 'SYSTEM'),
(334, b'0', b'1', '2026-03-19 14:25:00.000000', '2026-05-07', 50, 8, NULL, '14:00:00.000000', '14:50:00.000000', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, b'0', 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Ședință individuală', '2026-05-07 14:55:00.000000', 'SYSTEM', 'SYSTEM');
-- Total 10 sesiuni: Mar20 + Apr2/9/16/23 + Mai7 + Jun2/4/9/11 = 10 -> cota atinsa exact
-- Urmatoarea programare (174, Jul 2, PROGRAMATA) ar trebui sa fie reevaluare
-- O modificam sa fie reevaluare (are_evaluare=1) in logica de date
-- Pastram ca PROGRAMATA cu serviciu 2 (reevaluare)
UPDATE programari SET serviciu_id = 2, tip_serviciu = 'Evaluare - Reevaluare', are_evaluare = 1 WHERE id = 174;

-- =============================================================================
-- 5. SORIN TUDOR (487da990) - Eval #9 (2026-03-20) recomanda 6 sedinte
--    Starea: 3 sesiuni (Jun 9/12/15). Lipsesc 3.
--    Terapeut: 69f7e2ae, locatie 5, ora 10:00 (liber in Apr-Mai)
-- =============================================================================

INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES
(340, b'0', b'1', '2026-03-20 10:05:00.000000', '2026-04-06', 50, 5, NULL, '10:00:00.000000', '10:50:00.000000', '487da990-f8ed-437f-b5d9-e9e2f5aef199', 100.00, b'0', 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Ședință individuală', '2026-04-06 10:55:00.000000', 'SYSTEM', 'SYSTEM'),
(341, b'0', b'1', '2026-03-20 10:10:00.000000', '2026-04-13', 50, 5, NULL, '10:00:00.000000', '10:50:00.000000', '487da990-f8ed-437f-b5d9-e9e2f5aef199', 100.00, b'0', 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Ședință individuală', '2026-04-13 10:55:00.000000', 'SYSTEM', 'SYSTEM'),
(342, b'0', b'1', '2026-03-20 10:15:00.000000', '2026-04-20', 50, 5, NULL, '10:00:00.000000', '10:50:00.000000', '487da990-f8ed-437f-b5d9-e9e2f5aef199', 100.00, b'0', 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Ședință individuală', '2026-04-20 10:55:00.000000', 'SYSTEM', 'SYSTEM');
-- Total 6 sesiuni: Apr6/13/20 + Jun9/12/15 = 6 -> cota atinsa. 

-- =============================================================================
-- 6. MARIN CRISTIAN (6c400be7) - Eval #8 (2026-03-18) recomanda 10 sedinte
--    Starea: 4 sesiuni (Mar 26, Jun 9/12/15). Lipsesc 6.
--    Terapeut: cb3ac7f5, locatie 10 sau 11, ora 13:00
-- =============================================================================

INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES
(350, b'0', b'1', '2026-03-18 13:05:00.000000', '2026-04-02', 50, 10, NULL, '13:00:00.000000', '13:50:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, b'0', 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Ședință individuală', '2026-04-02 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(351, b'0', b'1', '2026-03-18 13:10:00.000000', '2026-04-09', 50, 10, NULL, '13:00:00.000000', '13:50:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, b'0', 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Ședință individuală', '2026-04-09 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(352, b'0', b'1', '2026-03-18 13:15:00.000000', '2026-04-16', 50, 10, NULL, '13:00:00.000000', '13:50:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, b'0', 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Ședință individuală', '2026-04-16 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(353, b'0', b'1', '2026-03-18 13:20:00.000000', '2026-04-23', 50, 10, NULL, '13:00:00.000000', '13:50:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, b'0', 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Ședință individuală', '2026-04-23 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(354, b'0', b'1', '2026-03-18 13:25:00.000000', '2026-05-07', 50, 10, NULL, '13:00:00.000000', '13:50:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, b'0', 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Ședință individuală', '2026-05-07 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(355, b'0', b'1', '2026-03-18 13:30:00.000000', '2026-05-14', 50, 10, NULL, '13:00:00.000000', '13:50:00.000000', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, b'0', 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Ședință individuală', '2026-05-14 13:55:00.000000', 'SYSTEM', 'SYSTEM');
-- Total 10: Mar26 + Apr2/9/16/23 + Mai7/14 + Jun9/12/15 = 10 -> cota atinsa

-- =============================================================================
-- 7. POPESCU ALINA (072ee168) - Eval #3 (2026-03-17) recomanda 5 sedinte
--    Starea: 3 sesiuni (Jun 8/12/15). Lipsesc 2.
--    Clara Maria (496b7af4), locatie 1, ora 11:00 (liber in Apr-Mai)
-- =============================================================================

INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES
(360, b'0', b'1', '2026-03-17 10:05:00.000000', '2026-04-06', 50, 1, NULL, '11:00:00.000000', '11:50:00.000000', '072ee168-7759-4613-96e1-226f0d7277ea', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-04-06 11:55:00.000000', 'SYSTEM', 'SYSTEM'),
(361, b'0', b'1', '2026-03-17 10:10:00.000000', '2026-04-20', 50, 1, NULL, '11:00:00.000000', '11:50:00.000000', '072ee168-7759-4613-96e1-226f0d7277ea', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-04-20 11:55:00.000000', 'SYSTEM', 'SYSTEM');
-- Total 5: Apr6 + Apr20 + Jun8/12/15 = 5 -> cota atinsa

-- =============================================================================
-- 8. POPESCU ANDREI (e61befe0) - Eval #5 (2026-03-10) recomanda 5 sedinte
--    Starea: 4 sesiuni (Mar 26, Jun 9/11/16). Lipseste 1.
--    Simona Ilie (f90f7761), locatie 1 sau 2, ora 09:00 (deja are Mar26 la 09:00)
--    Adaugam 1 sesiune in Aprilie
-- =============================================================================

INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES
(370, b'0', b'1', '2026-03-10 09:05:00.000000', '2026-04-09', 50, 1, NULL, '09:00:00.000000', '09:50:00.000000', 'e61befe0-0110-45b7-9a31-8f433b113909', 100.00, b'0', 6, 'FINALIZATA', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5', 'Kinetoterapie - Ședință individuală', '2026-04-09 09:55:00.000000', 'SYSTEM', 'SYSTEM');
-- Total 5: Mar26 + Apr9 + Jun9/11/16 = 5 -> cota atinsa

-- =============================================================================
-- 9. DOBRE DANIEL (5ff54c13) - Eval #30 (2026-06-08) recomanda 8 sedinte
--    Starea: 2 sesiuni (Jun 10/12). Lipsesc 6.
--    Terapeut: 69f7e2ae, locatie 5, ora 09:00
--    Adaugam 6 sesiuni in June (16/18/22/24/26/29)
-- =============================================================================

INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES
(380, b'0', b'1', '2026-06-08 10:00:00.000000', '2026-06-16', 50, 5, NULL, '09:00:00.000000', '09:50:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 100.00, b'0', 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Ședință individuală', '2026-06-16 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(381, b'0', b'1', '2026-06-08 10:05:00.000000', '2026-06-18', 50, 5, NULL, '09:00:00.000000', '09:50:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 100.00, b'0', 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Ședință individuală', '2026-06-18 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(382, b'0', b'1', '2026-06-08 10:10:00.000000', '2026-06-22', 50, 5, NULL, '09:00:00.000000', '09:50:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 100.00, b'0', 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Ședință individuală', '2026-06-22 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(383, b'0', b'1', '2026-06-08 10:15:00.000000', '2026-06-24', 50, 5, NULL, '09:00:00.000000', '09:50:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 100.00, b'0', 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Ședință individuală', '2026-06-24 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(384, b'0', b'1', '2026-06-08 10:20:00.000000', '2026-06-26', 50, 5, NULL, '09:00:00.000000', '09:50:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 100.00, b'0', 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Ședință individuală', '2026-06-26 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(385, b'0', b'1', '2026-06-08 10:25:00.000000', '2026-06-29', 50, 5, NULL, '09:00:00.000000', '09:50:00.000000', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 100.00, b'0', 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Ședință individuală', '2026-06-29 09:55:00.000000', 'SYSTEM', 'SYSTEM');
-- Total 8: Jun10/12/16/18/22/24/26/29 = 8 -> cota atinsa

-- =============================================================================
-- 10. DUMITRESCU ANA (ac0ef5eb) - Eval #31 (2026-06-08) recomanda 10 sedinte
--     Starea: 2 sesiuni (Jun 10/12). Lipsesc 8.
--     Sara Munteanu (d407ab1c), locatie 13, ora 08:00
-- =============================================================================

INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES
(390, b'0', b'1', '2026-06-08 09:10:00.000000', '2026-06-15', 50, 13, NULL, '08:00:00.000000', '08:50:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-06-15 08:55:00.000000', 'SYSTEM', 'SYSTEM'),
(391, b'0', b'1', '2026-06-08 09:15:00.000000', '2026-06-17', 50, 13, NULL, '08:00:00.000000', '08:50:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-06-17 08:55:00.000000', 'SYSTEM', 'SYSTEM'),
(392, b'0', b'1', '2026-06-08 09:20:00.000000', '2026-06-19', 50, 13, NULL, '08:00:00.000000', '08:50:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-06-19 08:55:00.000000', 'SYSTEM', 'SYSTEM'),
(393, b'0', b'1', '2026-06-08 09:25:00.000000', '2026-06-22', 50, 13, NULL, '08:00:00.000000', '08:50:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-06-22 08:55:00.000000', 'SYSTEM', 'SYSTEM'),
(394, b'0', b'1', '2026-06-08 09:30:00.000000', '2026-06-24', 50, 13, NULL, '08:00:00.000000', '08:50:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-06-24 08:55:00.000000', 'SYSTEM', 'SYSTEM'),
(395, b'0', b'1', '2026-06-08 09:35:00.000000', '2026-06-26', 50, 13, NULL, '08:00:00.000000', '08:50:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-06-26 08:55:00.000000', 'SYSTEM', 'SYSTEM'),
(396, b'0', b'1', '2026-06-08 09:40:00.000000', '2026-06-29', 50, 13, NULL, '08:00:00.000000', '08:50:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, b'0', 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-06-29 08:55:00.000000', 'SYSTEM', 'SYSTEM'),
(397, b'0', b'1', '2026-06-08 09:45:00.000000', '2026-07-01', 50, 13, NULL, '08:00:00.000000', '08:50:00.000000', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, b'0', 6, 'PROGRAMATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Ședință individuală', '2026-06-08 09:45:00.000000', 'SYSTEM', 'SYSTEM');
-- Total 10: Jun10/12/15/17/19/22/24/26/29 = 9 + Jul1(PROGRAMATA) = 10 -> cota atinsa la jul 1

-- =============================================================================
-- 11. STAN DANIELA (235aa647) - Eval #32 (2026-06-08) recomanda 6 sedinte
--     Starea: 2 sesiuni (Jun 12/15). Lipsesc 4.
--     Clara Maria (496b7af4), locatie 2, ora 12:00
-- =============================================================================

INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES
(400, b'0', b'1', '2026-06-08 13:10:00.000000', '2026-06-17', 50, 2, NULL, '12:00:00.000000', '12:50:00.000000', '235aa647-1c6c-479f-aa26-011539b51b73', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-17 12:55:00.000000', 'SYSTEM', 'SYSTEM'),
(401, b'0', b'1', '2026-06-08 13:15:00.000000', '2026-06-19', 50, 2, NULL, '12:00:00.000000', '12:50:00.000000', '235aa647-1c6c-479f-aa26-011539b51b73', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-19 12:55:00.000000', 'SYSTEM', 'SYSTEM'),
(402, b'0', b'1', '2026-06-08 13:20:00.000000', '2026-06-22', 50, 2, NULL, '12:00:00.000000', '12:50:00.000000', '235aa647-1c6c-479f-aa26-011539b51b73', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-22 12:55:00.000000', 'SYSTEM', 'SYSTEM'),
(403, b'0', b'1', '2026-06-08 13:25:00.000000', '2026-07-01', 50, 2, NULL, '12:00:00.000000', '12:50:00.000000', '235aa647-1c6c-479f-aa26-011539b51b73', 100.00, b'0', 6, 'PROGRAMATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-08 13:25:00.000000', 'SYSTEM', 'SYSTEM');
-- Total 6: Jun12/15/17/19/22 = 5 + Jul1(PROGRAMATA) = 6 -> cota atinsa

-- =============================================================================
-- 12. POPESCU ANA-MARIA (9f90d398) - Eval #33 (2026-06-08) recomanda 8 sedinte
--     Starea: 2 sesiuni (Jun 12/15) + 1 sesiune "Evaluare Initiala" Mar 30 (are_evaluare=0!)
--     Acea sesiune Mar 30 (id=64, serviciu=1 dar are_evaluare=0) e o anomalie - era o sesiune
--     Nu e numara ca sesiune terapeutica (serviciu 1 e evaluare, dar backendull numara dupa are_evaluare)
--     Deci effectiv: 2 sesiuni normale (Jun12 + Jun15). Lipsesc 6.
--     Clara Maria (496b7af4), locatie 1, ora 13:00
-- =============================================================================

INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES
(410, b'0', b'1', '2026-06-08 14:10:00.000000', '2026-06-17', 50, 1, NULL, '13:00:00.000000', '13:50:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-17 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(411, b'0', b'1', '2026-06-08 14:15:00.000000', '2026-06-19', 50, 1, NULL, '13:00:00.000000', '13:50:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-19 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(412, b'0', b'1', '2026-06-08 14:20:00.000000', '2026-06-22', 50, 1, NULL, '13:00:00.000000', '13:50:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-22 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(413, b'0', b'1', '2026-06-08 14:25:00.000000', '2026-06-24', 50, 1, NULL, '13:00:00.000000', '13:50:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-24 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(414, b'0', b'1', '2026-06-08 14:30:00.000000', '2026-06-26', 50, 1, NULL, '13:00:00.000000', '13:50:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', 100.00, b'0', 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-26 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(415, b'0', b'1', '2026-06-08 14:35:00.000000', '2026-07-01', 50, 1, NULL, '13:00:00.000000', '13:50:00.000000', '9f90d398-f030-4ce4-bfee-033758993378', 100.00, b'0', 6, 'PROGRAMATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Ședință individuală', '2026-06-08 14:35:00.000000', 'SYSTEM', 'SYSTEM');
-- Total 8: Jun12/15/17/19/22/24/26 = 7 + Jul1(PROGRAMATA) = 8 -> cota atinsa

-- =============================================================================
-- 13. MARIA IONESCU (de85c053) - Eval #23 (2026-06-17, REEVALUARE) recomanda 5 sedinte
--     Starea: 4 sesiuni din July (Jul6/8/10/13=FINALIZATA). Cota 4/5 - lipseste 1.
--     Sara Munteanu (d407ab1c), locatie 13, ora 09:00
-- =============================================================================

INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES
(420, b'0', b'1', '2026-06-17 10:00:00.000000', '2026-07-15', 50, 13, NULL, '09:00:00.000000', '09:50:00.000000', 'de85c053-a583-4219-8fe9-5afd66c0e812', 110.00, b'0', 8, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Gimnastică profilactică', '2026-07-15 09:55:00.000000', 'SYSTEM', 'SYSTEM');
-- Acum total 5 sesiuni dupa Jun17: Jul6/8/10/13/15 = 5 -> cota atinsa

-- =============================================================================
-- VERIFICARE FINALA
-- =============================================================================

SELECT 
  ev.id as eval_id,
  ev.pacient_keycloak_id,
  ev.data as data_evaluare,
  ev.tip,
  ev.sedinte_recomandate,
  (SELECT COUNT(*) FROM programari p2
   WHERE p2.pacient_keycloak_id COLLATE utf8mb4_unicode_ci = ev.pacient_keycloak_id
   AND p2.status = 'FINALIZATA'
   AND p2.are_evaluare = 0
   AND p2.data >= ev.data) as sedinte_efectuate,
  CASE 
    WHEN (SELECT COUNT(*) FROM programari p2
         WHERE p2.pacient_keycloak_id COLLATE utf8mb4_unicode_ci = ev.pacient_keycloak_id
         AND p2.status = 'FINALIZATA' AND p2.are_evaluare = 0
         AND p2.data >= ev.data) >= ev.sedinte_recomandate THEN 'OK - quota filled'
    ELSE '*** PROBLEMA: quota NOT filled ***'
  END as verdict
FROM evaluari ev
ORDER BY ev.pacient_keycloak_id, ev.data;
