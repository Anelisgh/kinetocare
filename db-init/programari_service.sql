-- MySQL dump 10.13  Distrib 8.0.35, for Win64 (x86_64)
--
-- Host: localhost    Database: programari_service
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `programari_service`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `programari_service` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `programari_service`;

--
-- Table structure for table `evaluari`
--

DROP TABLE IF EXISTS `evaluari`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluari` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `data` date NOT NULL,
  `diagnostic` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `observatii` text COLLATE utf8mb4_unicode_ci,
  `pacient_keycloak_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `programare_id` bigint DEFAULT NULL,
  `sedinte_recomandate` int DEFAULT NULL,
  `serviciu_recomandat_id` bigint DEFAULT NULL,
  `terapeut_keycloak_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tip` enum('INITIALA','REEVALUARE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `created_by` varchar(36) DEFAULT 'SYSTEM',
  `last_modified_by` varchar(36) DEFAULT 'SYSTEM',
  PRIMARY KEY (`id`),
  KEY `idx_eval_pacient` (`pacient_keycloak_id`),
  KEY `idx_eval_terapeut` (`terapeut_keycloak_id`),
  KEY `idx_eval_programare` (`programare_id`),
  KEY `idx_eval_tip` (`tip`),
  KEY `idx_eval_pacient_data` (`pacient_keycloak_id`,`data`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluari`
--

LOCK TABLES `evaluari` WRITE;
/*!40000 ALTER TABLE `evaluari` DISABLE KEYS */;
INSERT INTO `evaluari` (`id`, `created_at`, `data`, `diagnostic`, `observatii`, `pacient_keycloak_id`, `programare_id`, `sedinte_recomandate`, `serviciu_recomandat_id`, `terapeut_keycloak_id`, `tip`, `updated_at`) VALUES (1,'2026-02-28 19:00:42.292059','2026-02-27','scolioza','Prezinta dureri la spate nu stiu','2733282b-1ee8-4afb-827a-aa0ff0b98f61',1,3,3,'496b7af4-d378-418d-8d5f-5647ed31d11d','INITIALA','2026-02-28 19:00:42.292059'),(2,'2026-03-15 17:30:46.429070','2026-03-03','scolioza','','2733282b-1ee8-4afb-827a-aa0ff0b98f61',7,3,3,'496b7af4-d378-418d-8d5f-5647ed31d11d','REEVALUARE','2026-03-16 18:23:03.286194'),(3,'2026-03-17 16:51:33.333250','2026-03-17','dorsalgie','','072ee168-7759-4613-96e1-226f0d7277ea',9,5,3,'496b7af4-d378-418d-8d5f-5647ed31d11d','INITIALA','2026-03-17 16:51:33.333250'),(4,'2026-03-05 09:35:00.000000','2026-03-05','Hernie de disc L4-L5','Postură asimetrică la nivelul umerilor și bazinului, cu rotație toracală vizibilă. Mobilitate redusă în flexia laterală stângă. Pacientul raportează disconfort ocazional în zona lombară la efort prelungit.','77d841ba-ba92-4d98-a6bd-873c58fbc440',10,10,6,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','INITIALA','2026-03-05 09:35:00.000000'),(5,'2026-03-10 09:35:00.000000','2026-03-10','Cifoscolioză','Asimetrie scapulară evidentă. Tensiune crescută pe musculatura paravertebrală dreaptă. Necesită corectare posturală activă și stretching.','e61befe0-0110-45b7-9a31-8f433b113909',12,5,6,'f90f7761-c2ee-41f5-92fb-96c47b6567f5','INITIALA','2026-03-10 09:35:00.000000'),(6,'2026-03-10 09:35:00.000000','2026-02-16','Lombosciatica dreapta','Pacienta prezinta durere ascutita la flexia trunchiului. Testul Lasegue pozitiv la 45 grade. Deficit usor de forta pe flexia dorsala.','31d03fc1-1800-4a52-9817-0c6b21d78e14',17,10,6,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','INITIALA','2026-03-10 09:35:00.000000'),(7,'2026-03-12 08:35:00.000000','2026-03-12','Tendinită ahiliană','Durere acută la palpare și la flexia dorsală a gleznei stângi. Mers antalgic ușor șchiopătat observat la intrarea în cabinet.','6da1727a-5388-4bfb-b845-d96485c61498',20,8,6,'d407ab1c-7554-456d-9726-3f5571fa8fd0','INITIALA','2026-03-12 08:35:00.000000'),(8,'2026-03-18 12:35:00.000000','2026-03-18','Sindrom patelofemural','Crepitus palpabil la nivelul genunchiului drept în timpul genuflexiunilor. Slăbiciune marcată a vastului medial (VMO).','6c400be7-84b0-40f9-8854-b90e6c8358d9',22,10,6,'cb3ac7f5-4730-44cf-a053-d17f451f4b02','INITIALA','2026-03-18 12:35:00.000000'),(9,'2026-03-20 10:35:00.000000','2026-03-20','Fasciită plantară','Durere matinală severă declarată la primii pași. Retracție semnificativă pe lanțul fascial posterior. Fascia de la piciorul drept îngroșată la palpare.','487da990-f8ed-437f-b5d9-e9e2f5aef199',24,6,6,'69f7e2ae-39c0-4506-8558-d782493821a0','INITIALA','2026-03-20 10:35:00.000000'),(10,'2026-03-23 11:35:00.000000','2026-03-23','Cervicalgie miotensivă','Contractură marcată a mușchiului trapez bilateral. Tensiune ridicată pe musculatura suboccipitală, cel mai probabil indusă de lucrul prelungit la birou cu postură incorectă.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',25,5,7,'51abc54c-0c2f-4f0a-aead-bdaa9627661e','INITIALA','2026-03-23 11:35:00.000000'),(11,'2026-03-19 14:30:00.000000','2026-03-19','Lombosciatică stângă','Durere lombară cu iradiere pe membrul inferior stâng. Test Lasegue pozitiv la 50 de grade. Recomand începerea exercițiilor de decompresie și tonifiere progresivă, folosind rezistență elastică.','5f89ace3-454d-486a-accd-986caa20d84a',37,10,6,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','INITIALA','2026-03-19 14:30:00.000000'),(12,'2026-02-16 09:40:00.000000','2026-02-16','Entorsă gleznă grad II','Edem moderat la nivelul maleolei externe. Mobilitate redusă în flexie dorsală. Pacienta a început recuperarea după 3 săptămâni de imobilizare.','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',38,12,6,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','INITIALA','2026-02-16 09:40:00.000000'),(13,'2026-02-19 14:40:00.000000','2026-02-19','Cifoză dorsală','Tensiune pe musculatura pectorală. Spate rotund vizibil în repaus. Pacientul nu are istoric sportiv și lucrează foarte mult la birou.','00179244-a86e-4ade-8e22-3d70af6de57d',51,7,6,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','INITIALA','2026-03-25 18:31:04.488551'),(14,'2026-03-25 18:42:57.750882','2026-03-25','Cifoză dorsală','Pacientul este cooperant și a respectat parțial recomandările de exerciții pentru acasă. Se menține tendința de poziție cifotică în perioadele prelungite de lucru la birou.','00179244-a86e-4ade-8e22-3d70af6de57d',66,5,6,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','REEVALUARE','2026-03-25 18:42:57.750882');
INSERT INTO `evaluari` (`id`, `created_at`, `data`, `diagnostic`, `observatii`, `pacient_keycloak_id`, `programare_id`, `sedinte_recomandate`, `serviciu_recomandat_id`, `terapeut_keycloak_id`, `tip`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
(21,'2026-06-12 10:35:00.000000','2026-06-12','Cervicalgie miotensivă - Ameliorată rapid','Mobilitatea cervicala este complet restaurata, fara dureri la miscarile de rotatie. Spasmele pe trapez s-au redus considerabil. Recomand 4 sedinte individuale de stabilizare posturala pentru preventie.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',107,4,6,'51abc54c-0c2f-4f0a-aead-bdaa9627661e','REEVALUARE','2026-06-12 10:35:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(22,'2026-06-01 09:35:00.000000','2026-06-01','Lombalgie cronică','Pacienta acuza dureri lombare difuze, accentuate dupa perioade lungi de sezut la birou. Mobilitatea coloanei este redusa in flexie. Forta musculara abdominala este scazuta. Recomand un program de 6 sedinte individuale axate pe decompresie si tonifierea musculaturii paravertebrale.','de85c053-a583-4219-8fe9-5afd66c0e812',113,6,6,'d407ab1c-7554-456d-9726-3f5571fa8fd0','INITIALA','2026-06-01 09:35:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(23,'2026-06-17 09:35:00.000000','2026-06-17','Lombalgie cronică - Ameliorată','Durerea a scazut semnificativ (de la 7 la 1). Controlul motor al bazinului s-a imbunatatit. Recomand continuarea cu 5 sedinte de gimnastica profilactica pentru mentinere si educatie posturala.','de85c053-a583-4219-8fe9-5afd66c0e812',120,5,8,'d407ab1c-7554-456d-9726-3f5571fa8fd0','REEVALUARE','2026-06-17 09:35:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(24,'2026-06-01 11:35:00.000000','2026-06-01','Hernie de disc L5-S1 (post-op)','Pacienta se prezinta la 6 saptamani post-operator. Prezinta frica de miscare (kineziofobie), rigiditate lombara si deficit de forta pe membrul inferior stang. Se recomanda 4 sedinte de recuperare post-operatorie focusate pe decompresie blanda si recastigarea mobilitatii neuro-musculare.','8bd48465-32ec-413b-9985-9a57a03a3d88',126,4,7,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','INITIALA','2026-06-01 11:35:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(25,'2026-06-09 11:35:00.000000','2026-06-09','Hernie de disc L5-S1 (post-op) - Evoluție pozitivă','Mobilitatea generala s-a imbunatatit. Kineziofobia s-a redus substantial. Pacienta executa tranzitii posturale corecte. Recomand un nou program de 4 sedinte individuale de kinetoterapie pentru stabilizare activa.','8bd48465-32ec-413b-9985-9a57a03a3d88',131,4,6,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','REEVALUARE','2026-06-09 11:35:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af');

-- ==============================================================================
-- EVALUARI CLINICE PENTRU PACIENTII LUI ALEXANDRU DUMITRESCU (JUNE-JULY)
-- ==============================================================================
INSERT INTO `evaluari` (`id`, `created_at`, `data`, `diagnostic`, `observatii`, `pacient_keycloak_id`, `programare_id`, `sedinte_recomandate`, `serviciu_recomandat_id`, `terapeut_keycloak_id`, `tip`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
-- Mihnea Matei reevaluare
(26, '2026-06-10 15:35:00.000000', '2026-06-10', 'Cervicalgie miotensivă - Ameliorată', 'Control postural optim. Forta paravertebrala cervicala refacuta complet. Pacientul incepe planul profilactic de mentinere (3 sedinte recomandate).', '00179244-a86e-4ade-8e22-3d70af6de57d', 144, 3, 8, '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'REEVALUARE', '2026-06-10 15:35:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
-- Irina Ionescu reevaluare (pauza din martie)
(27, '2026-06-02 10:35:00.000000', '2026-06-02', 'Entorsă gleznă grad II - Mobilitate redusă', 'Stabilitatea gleznei a scazut in perioada de inactivitate. Mobilitatea in flexie dorsala este limitata si insotita de jena la 15 grade. Se recomanda 5 sedinte de kinetoterapie pentru proprioceptie.', 'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70', 150, 5, 6, '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'REEVALUARE', '2026-06-02 10:35:00.000000', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', '05e0f10e-2c4d-405e-abac-5ccedb83d2af');

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
/*!40000 ALTER TABLE `evaluari` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evolutii`
--

DROP TABLE IF EXISTS `evolutii`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evolutii` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `observatii` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `pacient_keycloak_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `terapeut_keycloak_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(36) DEFAULT 'SYSTEM',
  `last_modified_by` varchar(36) DEFAULT 'SYSTEM',
  PRIMARY KEY (`id`),
  KEY `idx_evolutie_pacient` (`pacient_keycloak_id`),
  KEY `idx_evolutie_terapeut` (`terapeut_keycloak_id`),
  KEY `idx_evolutie_pacient_terapeut` (`pacient_keycloak_id`,`terapeut_keycloak_id`,`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evolutii`
--

LOCK TABLES `evolutii` WRITE;
/*!40000 ALTER TABLE `evolutii` DISABLE KEYS */;
INSERT INTO `evolutii` (`id`, `created_at`, `observatii`, `pacient_keycloak_id`, `terapeut_keycloak_id`, `updated_at`) VALUES (1,'2026-02-28 19:01:02.181851','abcdefghijklmn','2733282b-1ee8-4afb-827a-aa0ff0b98f61','496b7af4-d378-418d-8d5f-5647ed31d11d','2026-02-28 19:01:02.181851'),(2,'2026-03-16 18:24:46.152991','abc','2733282b-1ee8-4afb-827a-aa0ff0b98f61','496b7af4-d378-418d-8d5f-5647ed31d11d','2026-03-16 18:24:46.152991'),(3,'2026-03-20 11:00:00.000000','A efectuat exercițiile corect, dar are dificultăți la menținerea posturii. Necesită mai multă atenție pe controlul trunchiului și respirație diafragmatică în timpul efortului.','77d841ba-ba92-4d98-a6bd-873c58fbc440','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-03-20 11:00:00.000000'),(4,'2026-03-20 15:00:00.000000','Toleranță crescută la exercițiile cu rezistență elastică. Am progresat la lucrul cu greutăți mici. Durerea iradiantă pe picior a scăzut semnificativ.','5f89ace3-454d-486a-accd-986caa20d84a','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-19 15:00:00.000000'),(5,'2026-03-02 10:00:00.000000','Mobilitatea lombara s-a imbunatatit usor dupa exercitiile de decomprimare. Vom insista pe stretching activ.','31d03fc1-1800-4a52-9817-0c6b21d78e14','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-17 11:00:00.000000'),(6,'2026-03-19 10:00:00.000000','Am început protocolul de exerciții excentrice. Pacientul tolerează bine sarcina pe tendon, durerea raportată în timpul execuției este minimă.','6da1727a-5388-4bfb-b845-d96485c61498','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-03-19 10:00:00.000000'),(7,'2026-03-18 12:40:00.000000','Pacient deschis, înțelege importanța activării gluteilor. Am stabilit exerciții simple de făcut și acasă zilnic.','6c400be7-84b0-40f9-8854-b90e6c8358d9','cb3ac7f5-4730-44cf-a053-d17f451f4b02','2026-03-18 12:40:00.000000'),(8,'2026-03-16 10:00:00.000000','Pacienta a executat corect exercitiile de stabilizare lombara (Bird-Dog). Am crescut usor rezistenta. Progres vizibil pe mobilitate.','31d03fc1-1800-4a52-9817-0c6b21d78e14','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-24 10:05:00.000000'),(9,'2026-03-23 10:00:00.000000','Aproape de recuperare completa a mobilitatii. Pregatim planul de intretinere pe termen lung pentru acasa.','31d03fc1-1800-4a52-9817-0c6b21d78e14','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-23 10:00:00.000000'),(10,'2026-03-03 11:00:00.000000','Edemul a dispărut complet. Mersul pe plan drept s-a îmbunătățit, dar încă are ușoară instabilitate la coborârea scărilor.','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-03 11:00:00.000000'),(11,'2026-03-17 11:00:00.000000','Am introdus exerciții pe placa de propriocepție. Pacienta reacționează foarte bine la stimulii de dezechilibru ușor.','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-17 11:00:00.000000'),(12,'2026-03-06 16:00:00.000000','Înțelege greu activarea romboizilor. Compensăm prin exerciții ghidate tactil. Toleranță scăzută la efort.','00179244-a86e-4ade-8e22-3d70af6de57d','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-06 16:00:00.000000'),(13,'2026-03-20 16:00:00.000000','Postura începe să se corecteze. Pacientul relatează că resimte oboseală musculară mai puțin la birou. Continuăm pe tonifiere.','00179244-a86e-4ade-8e22-3d70af6de57d','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-20 16:00:00.000000');
INSERT INTO `evolutii` (`id`, `created_at`, `observatii`, `pacient_keycloak_id`, `terapeut_keycloak_id`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
(21,'2026-06-01 11:05:00.000000','Prima sedinta de kinetoterapie. Am lucrat pe mobilitate articulara pasiva si stretching bland. Pacienta are o usoara teama de durere, dar raspunde bine la indicatii.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-06-01 11:05:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(22,'2026-06-03 11:05:00.000000','Am introdus exercitii izometrice pentru stabilizare paravertebrala si cervicala. Contracturile musculare din zona omoplatilor incep sa se relaxeze.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-06-03 11:05:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(23,'2026-06-05 11:05:00.000000','Mobilitatea cervicala a progresat. Pacienta raporteaza ca durerea matinala a scazut in intensitate. Am crescut numarul de repetari la exercitiile posturale.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-06-05 11:05:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(24,'2026-06-09 11:05:00.000000','Sedinta reprogramata. Pacienta a fost obosita, dar am putut efectua programul complet. Control postural bun la oglinda.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-06-09 11:05:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(25,'2026-06-10 11:05:00.000000','Sedinta 5 finalizata. Cota prescribed atinsa. Pacienta are o postura mult imbunatatita, durerea este aproape absenta. Urmeaza reevaluarea programata.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-06-10 11:05:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(26,'2026-07-06 10:55:00.000000','Inceputul etapei de stabilizare activa (Ciclul II). Am lucrat pe core-stability si exercitii cu banda elastica pentru umeri. Fara simptome dureroase.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-06 10:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(27,'2026-07-08 10:55:00.000000','Echilibru bun la exercitiile pe suprafete instabile. Corectii minime necesare pe alinierea trunchiului.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-08 10:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(28,'2026-07-10 10:55:00.000000','Toleranta mare la efort. Pacienta efectueaza si acasa planul de intretinere. Postura stabila.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-10 10:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(29,'2026-07-15 10:55:00.000000','Sedinta 4 finalizata. Obiectivele clinice au fost indeplinite integral. Pacienta este externata cu recomandari clare de mentinere pe termen lung.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-15 10:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(30,'2026-06-03 09:55:00.000000','Prima sedinta de kinetoterapie pentru lombalgie. Mobilizari blande pe flexie/extensie si exercitii usoare de respiratie. Durere locala declarata la efort.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-06-03 09:55:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(31,'2026-06-05 09:55:00.000000','Contractura paravertebrala incepe sa se diminueze. Am inceput tonifierea musculaturii abdominale profunde (transversul abdominal).','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-06-05 09:55:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(32,'2026-06-08 09:55:00.000000','Pacienta raporteaza ameliorarea semnificativa a simptomelor la birou. Am adaugat exercitii din pozitia patrupedie.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-06-08 09:55:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(33,'2026-06-10 09:55:00.000000','Executie buna a exercitiilor de tip "plank" adaptate. Rezistenta musculara crescuta pe zona lombara.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-06-10 09:55:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(34,'2026-06-12 09:55:00.000000','Fara episoade de durere acuta in ultima saptamana. Lucram intens pe mobilitatea soldurilor si stretching active pe lantul posterior.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-06-12 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(35,'2026-06-15 09:55:00.000000','Ultima sedinta din planul initial. Pacienta descrie o stare generala excelenta, avand mobilitate completa si absenta durerilor zilnice.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-06-15 09:55:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(36,'2026-07-06 09:55:00.000000','Inceperea sedintelor de gimnastica profilactica (mentinere). Focusul este mentinerea tonusului si constientizarea posturii corecte la birou.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-07-06 09:55:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(37,'2026-07-08 09:55:00.000000','Exercitii dinamice executate cu usurinta. Pacienta demonstreaza insusirea corecta a tehnicilor de ridicare a greutatilor din viata cotidiana.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-07-08 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(38,'2026-07-10 09:55:00.000000','Stretching intens al musculaturii flexoare de sold si pectorale. Postura globala deschisa, fara cifotizare.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-07-10 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(39,'2026-07-13 09:55:00.000000','Ultima sedinta din programul de mentinere. Pacientul relateaza ca se simte excelent, fara dureri. Recomandari de mentinere oferite.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-07-13 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(40,'2026-06-02 12:05:00.000000','Prima sedinta post-operatorie. Exercitii foarte usoare in descarcare de greutate pe saltea. Pacienta prezinta rigiditate musculara, dar este foarte cooperanta.','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-06-02 12:05:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(41,'2026-06-04 16:05:00.000000','Sedinta desfasurata in Locatia 8. S-au efectuat mobilizari neuro-dinamice pentru nervul sciatic. Durerea iradiata pe picior s-a diminuat considerabil.','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-06-04 16:05:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(42,'2026-06-05 16:05:00.000000','Continuam cresterea amplitudinii de miscare in extensie si rotatie. Forta musculara pe cvadriceps si fesieri se imbunatateste treptat.','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-06-05 16:05:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(43,'2026-06-08 12:05:00.000000','Ultima sedinta din planul post-operator. Control motor optim pe trunchi. Amplitudinea articulara este sigura si stabile. Pacienta este gata pentru reevaluare.','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-06-08 12:05:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(44,'2026-07-08 11:55:00.000000','Pacienta a revenit in programul meu pentru faza de stabilizare activa. Am initiat exercitii specifice de tip core-stability si exercitii pe placa de echilibru.','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-08 11:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(45,'2026-07-10 11:55:00.000000','Rezistenta musculara crescuta la nivelul trunchiului. Corectii posturale minime, pacienta constientizeaza instantaneu abaterile de aliniere.','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-10 11:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(46,'2026-07-13 11:55:00.000000','Executie fara ezitari la exercitiile cu rezistenta elastica mare. Absenta totala a durerilor la activitatile zilnice de birou.','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-13 11:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(47,'2026-07-15 11:55:00.000000','Ultima sedinta finalizata. Faza de stabilizare post-operatorie incheiata cu succes. Pacienta prezinta autonomie completa si a primit planul de auto-intretinere.','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-15 11:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e');

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
/*!40000 ALTER TABLE `evolutii` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `programari`
--

DROP TABLE IF EXISTS `programari`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `programari` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `are_evaluare` bit(1) NOT NULL,
  `are_jurnal` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `data` date NOT NULL,
  `durata_minute` int NOT NULL,
  `locatie_id` bigint NOT NULL,
  `motiv_anulare` enum('ADMINISTRATIV','ANULAT_DE_PACIENT','ANULAT_DE_TERAPEUT','NEPREZENTARE') DEFAULT NULL,
  `ora_inceput` time(6) NOT NULL,
  `ora_sfarsit` time(6) NOT NULL,
  `pacient_keycloak_id` varchar(36) NOT NULL,
  `pret` decimal(10,2) NOT NULL,
  `prima_intalnire` bit(1) DEFAULT NULL,
  `serviciu_id` bigint NOT NULL,
  `status` enum('ANULATA','FINALIZATA','PROGRAMATA') NOT NULL,
  `terapeut_keycloak_id` varchar(36) NOT NULL,
  `tip_serviciu` varchar(100) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `created_by` varchar(36) DEFAULT 'SYSTEM',
  `last_modified_by` varchar(36) DEFAULT 'SYSTEM',
  PRIMARY KEY (`id`),
  KEY `idx_prog_terapeut_data_status` (`terapeut_keycloak_id`,`data`,`status`),
  KEY `idx_prog_pacient_data` (`pacient_keycloak_id`,`data`),
  KEY `idx_prog_status_data` (`status`,`data`),
  KEY `idx_prog_pacient_status_data` (`pacient_keycloak_id`,`status`,`data`,`ora_inceput`),
  UNIQUE KEY `uk_terapeut_data_ora` (`terapeut_keycloak_id`,`data`,`ora_inceput`),
  KEY `idx_prog_stats` (`locatie_id`,`data`,`pret`),
  KEY `idx_prog_overlap` (`terapeut_keycloak_id`,`data`,`ora_inceput`,`ora_sfarsit`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `programari`
--

LOCK TABLES `programari` WRITE;
/*!40000 ALTER TABLE `programari` DISABLE KEYS */;
INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`) VALUES (1,_binary '',_binary '','2026-02-28 18:54:07.898168','2026-02-27',30,1,NULL,'09:00:00.000000','09:30:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',100.00,_binary '',1,'FINALIZATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Evaluare Initiala - Evaluare Initiala','2026-02-28 19:11:49.361497'),(2,_binary '\0',_binary '','2026-03-02 15:10:52.474013','2026-02-27',45,1,NULL,'11:30:00.000000','12:15:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',150.00,_binary '\0',6,'FINALIZATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Kinetoterapie - Masaj','2026-03-16 17:11:20.676409'),(3,_binary '\0',_binary '','2026-03-02 17:19:06.000000','2026-02-28',45,1,NULL,'09:00:00.000000','09:45:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',150.00,_binary '\0',6,'FINALIZATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Kinetoterapie - Masaj','2026-03-16 17:11:36.371410'),(4,_binary '\0',_binary '','2026-03-02 17:19:06.000000','2026-02-28',45,1,NULL,'10:00:00.000000','10:45:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',150.00,_binary '\0',6,'FINALIZATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Kinetoterapie - Masaj','2026-03-16 17:11:45.813602'),(5,_binary '',_binary '\0','2026-03-02 15:28:41.894072','2026-03-03',30,1,'ANULAT_DE_PACIENT','09:00:00.000000','09:30:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',100.00,_binary '\0',2,'ANULATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Evaluare - Reevaluare','2026-03-02 15:31:11.757415'),(6,_binary '',_binary '\0','2026-03-02 15:36:13.728994','2026-03-03',30,1,'ANULAT_DE_PACIENT','11:00:00.000000','11:30:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',100.00,_binary '\0',2,'ANULATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Evaluare - Reevaluare','2026-03-02 15:49:05.252698'),(7,_binary '',_binary '','2026-03-02 16:07:41.858404','2026-03-03',30,1,NULL,'09:40:00.000000','10:10:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',100.00,_binary '\0',2,'FINALIZATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Evaluare - Reevaluare','2026-03-16 17:13:40.681471'),(8,_binary '\0',_binary '\0','2026-03-16 18:13:25.786179','2026-03-17',45,1,'ANULAT_DE_TERAPEUT','09:55:00.000000','10:40:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',150.00,_binary '\0',6,'ANULATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Kinetoterapie - Masaj','2026-03-16 18:22:12.576124'),(9,_binary '',_binary '\0','2026-03-16 18:28:57.399746','2026-03-17',30,1,NULL,'09:40:00.000000','10:10:00.000000','072ee168-7759-4613-96e1-226f0d7277ea',100.00,_binary '',1,'FINALIZATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Evaluare - Evaluare Initiala','2026-03-17 16:51:33.393048'),(10,_binary '',_binary '','2026-03-01 10:10:00.000000','2026-03-05',30,7,NULL,'09:00:00.000000','09:30:00.000000','77d841ba-ba92-4d98-a6bd-873c58fbc440',150.00,_binary '',1,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Initiala','2026-03-05 09:30:00.000000'),(11,_binary '\0',_binary '','2026-03-15 10:10:00.000000','2026-03-20',50,2,NULL,'10:00:00.000000','10:50:00.000000','77d841ba-ba92-4d98-a6bd-873c58fbc440',100.00,_binary '',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-03-20 10:50:00.000000'),(12,_binary '',_binary '','2026-03-05 10:10:00.000000','2026-03-10',30,1,NULL,'09:00:00.000000','09:30:00.000000','e61befe0-0110-45b7-9a31-8f433b113909',150.00,_binary '',1,'FINALIZATA','f90f7761-c2ee-41f5-92fb-96c47b6567f5','Evaluare - Evaluare Initiala','2026-03-10 09:30:00.000000'),(13,_binary '\0',_binary '\0','2026-03-20 10:00:00.000000','2026-03-26',50,1,NULL,'09:00:00.000000','09:50:00.000000','e61befe0-0110-45b7-9a31-8f433b113909',100.00,_binary '\0',6,'PROGRAMATA','f90f7761-c2ee-41f5-92fb-96c47b6567f5','Kinetoterapie - Ședință individuală','2026-03-20 10:00:00.000000'),(14,_binary '\0',_binary '','2026-03-10 12:10:00.000000','2026-03-20',50,8,NULL,'14:00:00.000000','14:50:00.000000','5f89ace3-454d-486a-accd-986caa20d84a',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-19 14:50:00.000000'),(15,_binary '\0',_binary '\0','2026-03-19 15:00:00.000000','2026-03-27',50,8,'ANULAT_DE_PACIENT','14:00:00.000000','14:50:00.000000','5f89ace3-454d-486a-accd-986caa20d84a',100.00,_binary '\0',6,'ANULATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-24 18:21:06.526258'),(16,_binary '',_binary '\0','2026-03-14 09:10:00.000000','2026-03-30',30,13,NULL,'08:00:00.000000','08:30:00.000000','ac0ef5eb-34df-4a8c-8f4a-e879428aed2d',150.00,_binary '',1,'PROGRAMATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Evaluare - Evaluare Initiala','2026-03-14 09:10:00.000000'),(17,_binary '',_binary '','2026-03-08 10:10:00.000000','2026-02-16',30,7,NULL,'09:00:00.000000','09:30:00.000000','31d03fc1-1800-4a52-9817-0c6b21d78e14',150.00,_binary '',1,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Initiala','2026-03-10 09:30:00.000000'),(18,_binary '\0',_binary '','2026-03-10 09:40:00.000000','2026-02-17',50,7,NULL,'09:00:00.000000','09:50:00.000000','31d03fc1-1800-4a52-9817-0c6b21d78e14',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Sedinta individuala','2026-03-17 10:50:00.000000'),(19,_binary '\0',_binary '','2026-03-17 11:00:00.000000','2026-02-23',50,7,NULL,'09:00:00.000000','09:50:00.000000','31d03fc1-1800-4a52-9817-0c6b21d78e14',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Sedinta individuala','2026-03-17 11:00:00.000000'),(20,_binary '',_binary '','2026-03-10 11:10:00.000000','2026-03-12',30,13,NULL,'08:00:00.000000','08:30:00.000000','6da1727a-5388-4bfb-b845-d96485c61498',150.00,_binary '',1,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Evaluare - Evaluare Inițială','2026-03-12 08:30:00.000000'),(21,_binary '\0',_binary '','2026-03-12 08:40:00.000000','2026-03-19',50,13,NULL,'09:00:00.000000','09:50:00.000000','6da1727a-5388-4bfb-b845-d96485c61498',100.00,_binary '\0',6,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Ședință individuală','2026-03-19 09:50:00.000000'),(22,_binary '',_binary '','2026-03-15 12:10:00.000000','2026-03-18',30,10,NULL,'12:00:00.000000','12:30:00.000000','6c400be7-84b0-40f9-8854-b90e6c8358d9',150.00,_binary '',1,'FINALIZATA','cb3ac7f5-4730-44cf-a053-d17f451f4b02','Evaluare - Evaluare Inițială','2026-03-18 12:30:00.000000'),(23,_binary '\0',_binary '\0','2026-03-18 12:40:00.000000','2026-03-26',50,10,NULL,'13:00:00.000000','13:50:00.000000','6c400be7-84b0-40f9-8854-b90e6c8358d9',100.00,_binary '\0',6,'PROGRAMATA','cb3ac7f5-4730-44cf-a053-d17f451f4b02','Kinetoterapie - Ședință individuală','2026-03-18 12:40:00.000000'),(24,_binary '',_binary '','2026-03-18 14:10:00.000000','2026-03-20',30,9,NULL,'10:00:00.000000','10:30:00.000000','487da990-f8ed-437f-b5d9-e9e2f5aef199',150.00,_binary '',1,'FINALIZATA','69f7e2ae-39c0-4506-8558-d782493821a0','Evaluare - Evaluare Inițială','2026-03-20 10:30:00.000000'),(25,_binary '',_binary '','2026-03-21 09:10:00.000000','2026-03-23',30,2,NULL,'11:00:00.000000','11:30:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',150.00,_binary '',1,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Evaluare - Evaluare Inițială','2026-03-23 11:30:00.000000'),(26,_binary '',_binary '\0','2026-03-24 10:10:00.000000','2026-03-27',30,1,NULL,'09:00:00.000000','09:30:00.000000','235aa647-1c6c-479f-aa26-011539b51b73',150.00,_binary '',1,'PROGRAMATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Evaluare - Evaluare Inițială','2026-03-24 10:10:00.000000'),(36,_binary '',_binary '\0','2026-03-24 10:00:00.000000','2026-03-30',30,7,NULL,'09:00:00.000000','09:30:00.000000','31d03fc1-1800-4a52-9817-0c6b21d78e14',100.00,_binary '\0',2,'PROGRAMATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Reevaluare','2026-03-24 10:00:00.000000'),(37,_binary '',_binary '\0','2026-03-24 18:21:11.785732','2026-03-19',30,8,NULL,'14:00:00.000000','14:30:00.000000','5f89ace3-454d-486a-accd-986caa20d84a',150.00,_binary '\0',1,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Initiala','2026-03-24 20:27:34.334925'),(38,_binary '',_binary '','2026-02-15 11:00:00.000000','2026-02-16',30,7,NULL,'10:00:00.000000','10:30:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',150.00,_binary '',1,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Inițială','2026-02-16 09:30:00.000000'),(39,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-02-17',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-02-17 10:50:00.000000'),(41,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-02-24',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-02-24 10:50:00.000000'),(42,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-03-02',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-02 09:50:00.000000'),(43,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-03-03',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-03 10:50:00.000000'),(44,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-03-09',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-09 09:50:00.000000'),(45,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-03-10',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-10 10:50:00.000000'),(46,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-03-16',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-16 09:50:00.000000'),(47,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-03-17',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-17 10:50:00.000000'),(48,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-03-23',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-23 09:50:00.000000'),(49,_binary '\0',_binary '\0','2026-02-16 10:00:00.000000','2026-03-24',50,7,'NEPREZENTARE','09:00:00.000000','09:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'ANULATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-25 19:11:21.000000'),(50,_binary '\0',_binary '\0','2026-02-16 10:00:00.000000','2026-03-30',50,7,'ANULAT_DE_PACIENT','10:05:00.000000','10:55:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'ANULATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-25 18:26:31.037939'),(51,_binary '',_binary '','2026-02-18 11:00:00.000000','2026-02-19',30,8,NULL,'15:00:00.000000','15:30:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',150.00,_binary '',1,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Inițială','2026-02-19 14:30:00.000000'),(52,_binary '\0',_binary '','2026-02-19 15:00:00.000000','2026-02-20',50,8,NULL,'15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-02-20 15:50:00.000000'),(55,_binary '\0',_binary '','2026-02-19 15:00:00.000000','2026-03-05',50,8,NULL,'15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-05 14:50:00.000000'),(56,_binary '\0',_binary '','2026-02-19 15:00:00.000000','2026-03-06',50,8,NULL,'15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-06 15:50:00.000000'),(57,_binary '\0',_binary '','2026-02-19 15:00:00.000000','2026-03-12',50,8,NULL,'15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-12 14:50:00.000000'),(58,_binary '\0',_binary '','2026-02-19 15:00:00.000000','2026-03-13',50,8,NULL,'15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-13 15:50:00.000000'),(59,_binary '\0',_binary '','2026-02-19 15:00:00.000000','2026-03-19',50,8,NULL,'15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-19 14:50:00.000000'),(60,_binary '\0',_binary '','2026-02-19 15:00:00.000000','2026-03-20',50,8,NULL,'15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-20 15:50:00.000000'),(61,_binary '\0',_binary '\0','2026-02-19 15:00:00.000000','2026-03-26',50,8,'ANULAT_DE_PACIENT','15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'ANULATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-25 18:30:11.538734'),(62,_binary '\0',_binary '\0','2026-03-25 17:37:53.860261','2026-03-28',30,7,'ANULAT_DE_PACIENT','09:00:00.000000','09:30:00.000000','9f90d398-f030-4ce4-bfee-033758993378',150.00,_binary '',1,'ANULATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Evaluare - Evaluare Initiala','2026-03-25 17:38:18.223909'),(63,_binary '\0',_binary '\0','2026-03-25 17:38:31.418367','2026-03-30',30,7,'ANULAT_DE_PACIENT','11:00:00.000000','11:30:00.000000','9f90d398-f030-4ce4-bfee-033758993378',150.00,_binary '',1,'ANULATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Initiala','2026-03-25 17:44:47.098658'),(64,_binary '\0',_binary '\0','2026-03-25 17:44:53.215191','2026-03-30',30,7,NULL,'11:40:00.000000','12:10:00.000000','9f90d398-f030-4ce4-bfee-033758993378',150.00,_binary '',1,'PROGRAMATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Initiala','2026-03-25 17:44:53.215191'),(65,_binary '\0',_binary '\0','2026-03-25 18:26:58.825338','2026-03-30',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'PROGRAMATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-25 18:26:58.826027'),(66,_binary '',_binary '\0','2026-03-25 18:32:50.676753','2026-03-25',30,8,NULL,'14:00:00.000000','14:30:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',2,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Reevaluare','2026-03-25 18:42:57.843328');
INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
(101,b'0',b'1','2026-05-28 10:00:00.000000','2026-06-01',60,2,NULL,'10:00:00.000000','11:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',150.00,b'0',7,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Recuperare post-operatorie','2026-06-01 11:05:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(102,b'0',b'1','2026-05-28 10:05:00.000000','2026-06-03',60,2,NULL,'10:00:00.000000','11:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',150.00,b'0',7,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Recuperare post-operatorie','2026-06-03 11:05:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(103,b'0',b'1','2026-05-28 10:10:00.000000','2026-06-05',60,2,NULL,'10:00:00.000000','11:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',150.00,b'0',7,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Recuperare post-operatorie','2026-06-05 11:05:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(104,b'0',b'0','2026-05-28 10:15:00.000000','2026-06-08',60,2,'ANULAT_DE_PACIENT','10:00:00.000000','11:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',150.00,b'0',7,'ANULATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Recuperare post-operatorie','2026-06-07 20:35:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),
(105,b'0',b'1','2026-06-07 20:35:00.000000','2026-06-09',60,2,NULL,'10:00:00.000000','11:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',150.00,b'0',7,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Recuperare post-operatorie','2026-06-09 11:05:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(106,b'0',b'1','2026-05-28 10:20:00.000000','2026-06-10',60,2,NULL,'10:00:00.000000','11:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',150.00,b'0',7,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Recuperare post-operatorie','2026-06-10 11:05:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(107,b'1',b'1','2026-06-10 11:15:00.000000','2026-06-12',30,2,NULL,'10:00:00.000000','10:30:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',100.00,b'0',2,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Evaluare - Reevaluare','2026-06-12 10:35:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(108,b'0',b'1','2026-06-12 11:00:00.000000','2026-07-06',50,2,NULL,'10:00:00.000000','10:50:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-06 10:55:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(109,b'0',b'1','2026-06-12 11:05:00.000000','2026-07-08',50,2,NULL,'10:00:00.000000','10:50:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-08 10:55:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(110,b'0',b'1','2026-06-12 11:10:00.000000','2026-07-10',50,2,NULL,'10:00:00.000000','10:50:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-10 10:55:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(111,b'0',b'0','2026-06-12 11:15:00.000000','2026-07-13',50,2,'ANULAT_DE_PACIENT','10:00:00.000000','10:50:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',100.00,b'0',6,'ANULATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-12 18:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),
(112,b'0',b'1','2026-07-12 18:15:00.000000','2026-07-15',50,2,NULL,'10:00:00.000000','10:50:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-15 10:55:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(113,b'1',b'1','2026-05-28 12:00:00.000000','2026-06-01',30,13,NULL,'09:00:00.000000','09:30:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',150.00,b'1',1,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Evaluare - Evaluare Inițială','2026-06-01 09:35:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(114,b'0',b'1','2026-06-01 09:45:00.000000','2026-06-03',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',100.00,b'0',6,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Ședință individuală','2026-06-03 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(115,b'0',b'1','2026-06-01 09:50:00.000000','2026-06-05',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',100.00,b'0',6,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Ședință individuală','2026-06-05 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(116,b'0',b'1','2026-06-01 09:55:00.000000','2026-06-08',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',100.00,b'0',6,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Ședință individuală','2026-06-08 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(117,b'0',b'1','2026-06-01 10:00:00.000000','2026-06-10',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',100.00,b'0',6,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Ședință individuală','2026-06-10 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(118,b'0',b'1','2026-06-01 10:05:00.000000','2026-06-12',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',100.00,b'0',6,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Ședință individuală','2026-06-12 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(119,b'0',b'1','2026-06-01 10:10:00.000000','2026-06-15',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',100.00,b'0',6,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Ședință individuală','2026-06-15 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(120,b'1',b'1','2026-06-15 10:00:00.000000','2026-06-17',30,13,NULL,'09:00:00.000000','09:30:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',100.00,b'0',2,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Evaluare - Reevaluare','2026-06-17 09:35:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(121,b'0',b'1','2026-06-17 09:40:00.000000','2026-07-06',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',110.00,b'0',8,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Gimnastică profilactică','2026-07-06 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(122,b'0',b'1','2026-06-17 09:45:00.000000','2026-07-08',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',110.00,b'0',8,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Gimnastică profilactică','2026-07-08 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(123,b'0',b'1','2026-06-17 09:50:00.000000','2026-07-10',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',110.00,b'0',8,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Gimnastică profilactică','2026-07-10 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(124,b'0',b'1','2026-06-17 09:55:00.000000','2026-07-13',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',110.00,b'0',8,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Gimnastică profilactică','2026-07-13 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(125,b'0',b'0','2026-06-17 10:00:00.000000','2026-07-15',50,13,'ANULAT_DE_PACIENT','09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',110.00,b'0',8,'ANULATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Gimnastică profilactică','2026-07-14 15:30:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(126,b'1',b'1','2026-05-28 14:00:00.000000','2026-06-01',30,7,NULL,'11:00:00.000000','11:30:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',150.00,b'1',1,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Inițială','2026-06-01 11:35:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(127,b'0',b'1','2026-06-01 11:45:00.000000','2026-06-02',60,7,NULL,'11:00:00.000000','12:00:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',150.00,b'0',7,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Recuperare post-operatorie','2026-06-02 12:05:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(128,b'0',b'1','2026-06-01 11:50:00.000000','2026-06-04',60,8,NULL,'15:00:00.000000','16:00:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',150.00,b'0',7,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Recuperare post-operatorie','2026-06-04 16:05:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(129,b'0',b'1','2026-06-01 11:55:00.000000','2026-06-05',60,8,NULL,'15:00:00.000000','16:00:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',150.00,b'0',7,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Recuperare post-operatorie','2026-06-05 16:05:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(130,b'0',b'1','2026-06-01 12:00:00.000000','2026-06-08',60,7,NULL,'11:00:00.000000','12:00:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',150.00,b'0',7,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Recuperare post-operatorie','2026-06-08 12:05:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(131,b'1',b'1','2026-06-08 12:15:00.000000','2026-06-09',30,7,NULL,'11:00:00.000000','11:30:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',100.00,b'0',2,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Reevaluare','2026-06-09 11:35:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(132,b'0',b'0','2026-06-09 12:00:00.000000','2026-06-15',60,7,'ANULAT_DE_TERAPEUT','11:00:00.000000','12:00:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',150.00,b'0',7,'ANULATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Recuperare post-operatorie','2026-06-14 09:00:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(133,b'0',b'1','2026-07-01 10:30:00.000000','2026-07-08',50,2,NULL,'11:00:00.000000','11:50:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-08 11:55:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(134,b'0',b'1','2026-07-01 10:35:00.000000','2026-07-10',50,2,NULL,'11:00:00.000000','11:50:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-10 11:55:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(135,b'0',b'1','2026-07-01 10:40:00.000000','2026-07-13',50,2,NULL,'11:00:00.000000','11:50:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-13 11:55:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(136,b'0',b'1','2026-07-01 10:45:00.000000','2026-07-15',50,2,NULL,'11:00:00.000000','11:50:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-15 11:55:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e');

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
/*!40000 ALTER TABLE `programari` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `relatie_pacient_terapeut`
--

DROP TABLE IF EXISTS `relatie_pacient_terapeut`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `relatie_pacient_terapeut` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activa` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `data_inceput` date NOT NULL,
  `data_sfarsit` date DEFAULT NULL,
  `pacient_keycloak_id` varchar(36) NOT NULL,
  `terapeut_keycloak_id` varchar(36) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `created_by` varchar(36) DEFAULT 'SYSTEM',
  `last_modified_by` varchar(36) DEFAULT 'SYSTEM',
  PRIMARY KEY (`id`),
  KEY `idx_rel_pacient` (`pacient_keycloak_id`),
  KEY `idx_rel_terapeut` (`terapeut_keycloak_id`),
  KEY `idx_rel_activa` (`activa`),
  KEY `idx_rel_pacient_activa` (`pacient_keycloak_id`,`activa`),
  KEY `idx_rel_terapeut_activa` (`terapeut_keycloak_id`,`activa`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relatie_pacient_terapeut`
--

LOCK TABLES `relatie_pacient_terapeut` WRITE;
/*!40000 ALTER TABLE `relatie_pacient_terapeut` DISABLE KEYS */;
INSERT INTO `relatie_pacient_terapeut` (`id`, `activa`, `created_at`, `data_inceput`, `data_sfarsit`, `pacient_keycloak_id`, `terapeut_keycloak_id`, `updated_at`) VALUES (1,_binary '','2026-02-28 18:58:03.495685','2026-02-27',NULL,'2733282b-1ee8-4afb-827a-aa0ff0b98f61','496b7af4-d378-418d-8d5f-5647ed31d11d','2026-02-28 18:58:03.495685'),(2,_binary '','2026-03-17 16:19:38.392485','2026-03-17',NULL,'072ee168-7759-4613-96e1-226f0d7277ea','496b7af4-d378-418d-8d5f-5647ed31d11d','2026-03-17 16:19:38.393680'),(3,_binary '\0','2026-03-01 10:00:00.000000','2026-03-01','2026-03-15','77d841ba-ba92-4d98-a6bd-873c58fbc440','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-15 10:00:00.000000'),(4,_binary '','2026-03-15 10:05:00.000000','2026-03-15',NULL,'77d841ba-ba92-4d98-a6bd-873c58fbc440','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-03-15 10:05:00.000000'),(5,_binary '\0','2026-03-02 09:00:00.000000','2026-03-02','2026-03-18','5ff54c13-8ec1-40b3-b147-bbf2ad48fb86','cb3ac7f5-4730-44cf-a053-d17f451f4b02','2026-03-18 09:00:00.000000'),(6,_binary '','2026-03-18 09:10:00.000000','2026-03-18',NULL,'5ff54c13-8ec1-40b3-b147-bbf2ad48fb86','69f7e2ae-39c0-4506-8558-d782493821a0','2026-03-18 09:10:00.000000'),(7,_binary '','2026-03-05 10:00:00.000000','2026-03-05',NULL,'e61befe0-0110-45b7-9a31-8f433b113909','f90f7761-c2ee-41f5-92fb-96c47b6567f5','2026-03-05 10:00:00.000000'),(8,_binary '','2026-03-06 11:00:00.000000','2026-03-06',NULL,'de85c053-a583-4219-8fe9-5afd66c0e812','69f7e2ae-39c0-4506-8558-d782493821a0','2026-03-06 11:00:00.000000'),(9,_binary '','2026-03-10 12:00:00.000000','2026-03-10',NULL,'5f89ace3-454d-486a-accd-986caa20d84a','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-10 12:00:00.000000'),(10,_binary '','2026-03-12 14:00:00.000000','2026-03-12',NULL,'8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-03-12 14:00:00.000000'),(11,_binary '','2026-03-14 09:00:00.000000','2026-03-14',NULL,'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-03-14 09:00:00.000000'),(12,_binary '','2026-03-08 10:00:00.000000','2026-03-08',NULL,'31d03fc1-1800-4a52-9817-0c6b21d78e14','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-08 10:00:00.000000'),(13,_binary '','2026-03-10 11:00:00.000000','2026-03-10',NULL,'6da1727a-5388-4bfb-b845-d96485c61498','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-03-10 11:00:00.000000'),(14,_binary '','2026-03-15 12:00:00.000000','2026-03-15',NULL,'6c400be7-84b0-40f9-8854-b90e6c8358d9','cb3ac7f5-4730-44cf-a053-d17f451f4b02','2026-03-15 12:00:00.000000'),(15,_binary '','2026-03-18 14:00:00.000000','2026-03-18',NULL,'487da990-f8ed-437f-b5d9-e9e2f5aef199','69f7e2ae-39c0-4506-8558-d782493821a0','2026-03-18 14:00:00.000000'),(16,_binary '','2026-03-21 09:00:00.000000','2026-03-21',NULL,'a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-03-21 09:00:00.000000'),(17,_binary '','2026-03-24 10:00:00.000000','2026-03-24',NULL,'235aa647-1c6c-479f-aa26-011539b51b73','496b7af4-d378-418d-8d5f-5647ed31d11d','2026-03-24 10:00:00.000000'),(18,_binary '','2026-02-15 10:00:00.000000','2026-02-15',NULL,'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-02-15 10:00:00.000000'),(19,_binary '','2026-02-18 10:00:00.000000','2026-02-18',NULL,'00179244-a86e-4ade-8e22-3d70af6de57d','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-02-18 10:00:00.000000');
INSERT INTO `relatie_pacient_terapeut` (`id`, `activa`, `created_at`, `data_inceput`, `data_sfarsit`, `pacient_keycloak_id`, `terapeut_keycloak_id`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
(21,b'1','2026-06-01 09:00:00.000000','2026-06-01',NULL,'de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-06-01 09:00:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(22,b'1','2026-06-01 11:00:00.000000','2026-06-01',NULL,'8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-06-01 11:00:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(23,b'1','2026-07-01 10:00:00.000000','2026-07-01',NULL,'8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-01 10:00:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e');

-- Actualizari de relatii vechi
UPDATE `relatie_pacient_terapeut` SET `activa` = b'0', `data_sfarsit` = '2026-06-01', `updated_at` = '2026-06-01 09:00:00.000000', `last_modified_by` = 'd407ab1c-7554-456d-9726-3f5571fa8fd0' WHERE `id` = 8;
UPDATE `relatie_pacient_terapeut` SET `activa` = b'0', `data_sfarsit` = '2026-06-01', `updated_at` = '2026-06-01 11:00:00.000000', `last_modified_by` = '05e0f10e-2c4d-405e-abac-5ccedb83d2af' WHERE `id` = 10;
UPDATE `relatie_pacient_terapeut` SET `activa` = b'0', `data_sfarsit` = '2026-07-01', `updated_at` = '2026-07-01 10:00:00.000000', `last_modified_by` = '51abc54c-0c2f-4f0a-aead-bdaa9627661e' WHERE `id` = 22;

-- ==============================================================================
-- NOI RELATII ACTIVE PACIENT-TERAPEUT (JUNE)
-- ==============================================================================
INSERT INTO `relatie_pacient_terapeut` (`id`, `activa`, `created_at`, `data_inceput`, `data_sfarsit`, `pacient_keycloak_id`, `terapeut_keycloak_id`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
(30, b'1', '2026-06-01 09:00:00.000000', '2026-06-01', NULL, '9f90d398-f030-4ce4-bfee-033758993378', '496b7af4-d378-418d-8d5f-5647ed31d11d', '2026-06-01 09:00:00.000000', '496b7af4-d378-418d-8d5f-5647ed31d11d', '496b7af4-d378-418d-8d5f-5647ed31d11d');
/*!40000 ALTER TABLE `relatie_pacient_terapeut` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


-- ==============================================================================
-- FIX QUOTA VIOLATIONS - Respecta regula determinaServiciulCorect
-- countSedintePacientDupaData: status=FINALIZATA AND are_evaluare=false AND data>=data_eval
-- O reevaluare se adauga DOAR cand sedinteEfectuate >= sedinteRecomandate
-- ==============================================================================

-- Modifica cota evaluare Tudor Robert #7 (8->5, pentru ca avem exact 5 sesiuni inainte de June)
UPDATE `evaluari` SET `sedinte_recomandate` = 5 WHERE `id` = 7;

-- Fix programare 174 (Stan Mihai, Jul 2): trebuie sa fie reevaluare dupa 10 sesiuni
UPDATE `programari` SET `serviciu_id` = 2, `tip_serviciu` = 'Evaluare - Reevaluare', `are_evaluare` = 1 WHERE `id` = 174;

-- Adauga evaluare pentru reevaluarea Mihaela Neagu din Mar 30 (programare id=36)
INSERT INTO `evaluari` (`id`, `created_at`, `data`, `diagnostic`, `observatii`, `pacient_keycloak_id`, `programare_id`, `sedinte_recomandate`, `serviciu_recomandat_id`, `terapeut_keycloak_id`, `tip`, `updated_at`, `created_by`, `last_modified_by`) VALUES
(34, '2026-03-30 09:35:00.000000', '2026-03-30', 'Cervicalgie miotensiva - Ameliorata', 'Pacienta a finalizat cele 10 sedinte prescrise. Mobilitatea cervicala si postura generala s-au imbunatatit semnificativ. Se recomanda un program de mentinere de 5 sedinte individuale de kinetoterapie.', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 36, 5, 6, '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'REEVALUARE', '2026-03-30 09:35:00.000000', 'SYSTEM', 'SYSTEM');

-- Sesiuni suplimentare pentru completarea cotelor (inserate in ordine cronologica)
INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES
-- Mihaela Neagu: sesiunile 3-10 din cota de 10 (eval Feb 16), inainte de reevaluarea Mar 30
(300, 0, 1, '2026-02-25 09:05:00.000000', '2026-02-25', 50, 7, NULL, '09:00:00', '09:50:00', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, 0, 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Sedinta individuala', '2026-02-25 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(301, 0, 1, '2026-02-26 09:05:00.000000', '2026-02-26', 50, 7, NULL, '09:00:00', '09:50:00', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, 0, 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Sedinta individuala', '2026-02-26 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(302, 0, 1, '2026-03-04 09:05:00.000000', '2026-03-04', 50, 7, NULL, '09:00:00', '09:50:00', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, 0, 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Sedinta individuala', '2026-03-04 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(303, 0, 1, '2026-03-11 09:05:00.000000', '2026-03-11', 50, 7, NULL, '09:00:00', '09:50:00', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, 0, 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Sedinta individuala', '2026-03-11 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(304, 0, 1, '2026-03-18 09:05:00.000000', '2026-03-18', 50, 7, NULL, '09:00:00', '09:50:00', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, 0, 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Sedinta individuala', '2026-03-18 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(305, 0, 1, '2026-03-24 10:05:00.000000', '2026-03-24', 50, 7, NULL, '10:00:00', '10:50:00', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, 0, 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Sedinta individuala', '2026-03-24 10:55:00.000000', 'SYSTEM', 'SYSTEM'),
(306, 0, 1, '2026-03-26 09:05:00.000000', '2026-03-26', 50, 7, NULL, '09:00:00', '09:50:00', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, 0, 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Sedinta individuala', '2026-03-26 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(307, 0, 1, '2026-03-27 09:05:00.000000', '2026-03-27', 50, 7, NULL, '09:00:00', '09:50:00', '31d03fc1-1800-4a52-9817-0c6b21d78e14', 100.00, 0, 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Sedinta individuala', '2026-03-27 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
-- Tudor Robert: sesiunile 2-5 din cota de 5 (eval Mar 12), Aprilie
(310, 0, 1, '2026-03-12 09:00:00.000000', '2026-04-02', 50, 13, NULL, '09:00:00', '09:50:00', '6da1727a-5388-4bfb-b845-d96485c61498', 100.00, 0, 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Sedinta individuala', '2026-04-02 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(311, 0, 1, '2026-03-12 09:05:00.000000', '2026-04-07', 50, 13, NULL, '09:00:00', '09:50:00', '6da1727a-5388-4bfb-b845-d96485c61498', 100.00, 0, 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Sedinta individuala', '2026-04-07 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(312, 0, 1, '2026-03-12 09:10:00.000000', '2026-04-14', 50, 13, NULL, '09:00:00', '09:50:00', '6da1727a-5388-4bfb-b845-d96485c61498', 100.00, 0, 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Sedinta individuala', '2026-04-14 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(313, 0, 1, '2026-03-12 09:15:00.000000', '2026-04-21', 50, 13, NULL, '09:00:00', '09:50:00', '6da1727a-5388-4bfb-b845-d96485c61498', 100.00, 0, 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Sedinta individuala', '2026-04-21 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
-- Gheorghe Elena: sesiunile 2-7 din cota de 10 (eval Mar 5), Aprilie-Mai
(320, 0, 1, '2026-03-05 09:00:00.000000', '2026-04-06', 50, 2, NULL, '11:10:00', '12:00:00', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, 0, 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Sedinta individuala', '2026-04-06 12:05:00.000000', 'SYSTEM', 'SYSTEM'),
(321, 0, 1, '2026-03-05 09:05:00.000000', '2026-04-13', 50, 2, NULL, '11:10:00', '12:00:00', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, 0, 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Sedinta individuala', '2026-04-13 12:05:00.000000', 'SYSTEM', 'SYSTEM'),
(322, 0, 1, '2026-03-05 09:10:00.000000', '2026-04-20', 50, 2, NULL, '11:10:00', '12:00:00', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, 0, 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Sedinta individuala', '2026-04-20 12:05:00.000000', 'SYSTEM', 'SYSTEM'),
(323, 0, 1, '2026-03-05 09:15:00.000000', '2026-04-27', 50, 2, NULL, '11:10:00', '12:00:00', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, 0, 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Sedinta individuala', '2026-04-27 12:05:00.000000', 'SYSTEM', 'SYSTEM'),
(324, 0, 1, '2026-03-05 09:20:00.000000', '2026-05-11', 50, 2, NULL, '11:10:00', '12:00:00', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, 0, 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Sedinta individuala', '2026-05-11 12:05:00.000000', 'SYSTEM', 'SYSTEM'),
(325, 0, 1, '2026-03-05 09:25:00.000000', '2026-05-18', 50, 2, NULL, '11:10:00', '12:00:00', '77d841ba-ba92-4d98-a6bd-873c58fbc440', 100.00, 0, 6, 'FINALIZATA', '51abc54c-0c2f-4f0a-aead-bdaa9627661e', 'Kinetoterapie - Sedinta individuala', '2026-05-18 12:05:00.000000', 'SYSTEM', 'SYSTEM'),
-- Stan Mihai: sesiunile 2-6 din cota de 10 (eval Mar 19), Aprilie-Mai
(330, 0, 1, '2026-03-19 14:05:00.000000', '2026-04-02', 50, 8, NULL, '14:00:00', '14:50:00', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, 0, 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Sedinta individuala', '2026-04-02 14:55:00.000000', 'SYSTEM', 'SYSTEM'),
(331, 0, 1, '2026-03-19 14:10:00.000000', '2026-04-09', 50, 8, NULL, '14:00:00', '14:50:00', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, 0, 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Sedinta individuala', '2026-04-09 14:55:00.000000', 'SYSTEM', 'SYSTEM'),
(332, 0, 1, '2026-03-19 14:15:00.000000', '2026-04-16', 50, 8, NULL, '14:00:00', '14:50:00', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, 0, 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Sedinta individuala', '2026-04-16 14:55:00.000000', 'SYSTEM', 'SYSTEM'),
(333, 0, 1, '2026-03-19 14:20:00.000000', '2026-04-23', 50, 8, NULL, '14:00:00', '14:50:00', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, 0, 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Sedinta individuala', '2026-04-23 14:55:00.000000', 'SYSTEM', 'SYSTEM'),
(334, 0, 1, '2026-03-19 14:25:00.000000', '2026-05-07', 50, 8, NULL, '14:00:00', '14:50:00', '5f89ace3-454d-486a-accd-986caa20d84a', 100.00, 0, 6, 'FINALIZATA', '05e0f10e-2c4d-405e-abac-5ccedb83d2af', 'Kinetoterapie - Sedinta individuala', '2026-05-07 14:55:00.000000', 'SYSTEM', 'SYSTEM'),
-- Sorin Tudor: sesiunile 1-3 din cota de 6 (eval Mar 20), Aprilie
(340, 0, 1, '2026-03-20 10:05:00.000000', '2026-04-06', 50, 5, NULL, '10:00:00', '10:50:00', '487da990-f8ed-437f-b5d9-e9e2f5aef199', 100.00, 0, 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Sedinta individuala', '2026-04-06 10:55:00.000000', 'SYSTEM', 'SYSTEM'),
(341, 0, 1, '2026-03-20 10:10:00.000000', '2026-04-13', 50, 5, NULL, '10:00:00', '10:50:00', '487da990-f8ed-437f-b5d9-e9e2f5aef199', 100.00, 0, 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Sedinta individuala', '2026-04-13 10:55:00.000000', 'SYSTEM', 'SYSTEM'),
(342, 0, 1, '2026-03-20 10:15:00.000000', '2026-04-20', 50, 5, NULL, '10:00:00', '10:50:00', '487da990-f8ed-437f-b5d9-e9e2f5aef199', 100.00, 0, 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Sedinta individuala', '2026-04-20 10:55:00.000000', 'SYSTEM', 'SYSTEM'),
-- Marin Cristian: sesiunile 2-7 din cota de 10 (eval Mar 18), Aprilie-Mai
(350, 0, 1, '2026-03-18 13:05:00.000000', '2026-04-02', 50, 10, NULL, '13:00:00', '13:50:00', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, 0, 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Sedinta individuala', '2026-04-02 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(351, 0, 1, '2026-03-18 13:10:00.000000', '2026-04-09', 50, 10, NULL, '13:00:00', '13:50:00', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, 0, 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Sedinta individuala', '2026-04-09 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(352, 0, 1, '2026-03-18 13:15:00.000000', '2026-04-16', 50, 10, NULL, '13:00:00', '13:50:00', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, 0, 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Sedinta individuala', '2026-04-16 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(353, 0, 1, '2026-03-18 13:20:00.000000', '2026-04-23', 50, 10, NULL, '13:00:00', '13:50:00', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, 0, 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Sedinta individuala', '2026-04-23 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(354, 0, 1, '2026-03-18 13:25:00.000000', '2026-05-07', 50, 10, NULL, '13:00:00', '13:50:00', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, 0, 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Sedinta individuala', '2026-05-07 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(355, 0, 1, '2026-03-18 13:30:00.000000', '2026-05-14', 50, 10, NULL, '13:00:00', '13:50:00', '6c400be7-84b0-40f9-8854-b90e6c8358d9', 100.00, 0, 6, 'FINALIZATA', 'cb3ac7f5-4730-44cf-a053-d17f451f4b02', 'Kinetoterapie - Sedinta individuala', '2026-05-14 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
-- Popescu Alina: sesiunile 1-2 din cota de 5 (eval Mar 17), Aprilie
(360, 0, 1, '2026-03-17 10:05:00.000000', '2026-04-06', 50, 1, NULL, '11:00:00', '11:50:00', '072ee168-7759-4613-96e1-226f0d7277ea', 100.00, 0, 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Sedinta individuala', '2026-04-06 11:55:00.000000', 'SYSTEM', 'SYSTEM'),
(361, 0, 1, '2026-03-17 10:10:00.000000', '2026-04-20', 50, 1, NULL, '11:00:00', '11:50:00', '072ee168-7759-4613-96e1-226f0d7277ea', 100.00, 0, 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Sedinta individuala', '2026-04-20 11:55:00.000000', 'SYSTEM', 'SYSTEM'),
-- Popescu Andrei: sesiunea 2 din cota de 5 (eval Mar 10), Aprilie
(370, 0, 1, '2026-03-10 09:05:00.000000', '2026-04-09', 50, 1, NULL, '09:00:00', '09:50:00', 'e61befe0-0110-45b7-9a31-8f433b113909', 100.00, 0, 6, 'FINALIZATA', 'f90f7761-c2ee-41f5-92fb-96c47b6567f5', 'Kinetoterapie - Sedinta individuala', '2026-04-09 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
-- Dobre Daniel: sesiunile 3-8 din cota de 8 (eval Jun 8), Iunie
(380, 0, 1, '2026-06-08 10:00:00.000000', '2026-06-16', 50, 5, NULL, '09:00:00', '09:50:00', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 100.00, 0, 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Sedinta individuala', '2026-06-16 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(381, 0, 1, '2026-06-08 10:05:00.000000', '2026-06-18', 50, 5, NULL, '09:00:00', '09:50:00', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 100.00, 0, 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Sedinta individuala', '2026-06-18 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(382, 0, 1, '2026-06-08 10:10:00.000000', '2026-06-22', 50, 5, NULL, '09:00:00', '09:50:00', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 100.00, 0, 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Sedinta individuala', '2026-06-22 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(383, 0, 1, '2026-06-08 10:15:00.000000', '2026-06-24', 50, 5, NULL, '09:00:00', '09:50:00', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 100.00, 0, 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Sedinta individuala', '2026-06-24 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(384, 0, 1, '2026-06-08 10:20:00.000000', '2026-06-26', 50, 5, NULL, '09:00:00', '09:50:00', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 100.00, 0, 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Sedinta individuala', '2026-06-26 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
(385, 0, 1, '2026-06-08 10:25:00.000000', '2026-06-29', 50, 5, NULL, '09:00:00', '09:50:00', '5ff54c13-8ec1-40b3-b147-bbf2ad48fb86', 100.00, 0, 6, 'FINALIZATA', '69f7e2ae-39c0-4506-8558-d782493821a0', 'Kinetoterapie - Sedinta individuala', '2026-06-29 09:55:00.000000', 'SYSTEM', 'SYSTEM'),
-- Dumitrescu Ana: sesiunile 3-9 din cota de 10 (eval Jun 8) + 1 viitoare
(390, 0, 1, '2026-06-08 09:10:00.000000', '2026-06-15', 50, 13, NULL, '08:00:00', '08:50:00', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, 0, 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Sedinta individuala', '2026-06-15 08:55:00.000000', 'SYSTEM', 'SYSTEM'),
(391, 0, 1, '2026-06-08 09:15:00.000000', '2026-06-17', 50, 13, NULL, '08:00:00', '08:50:00', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, 0, 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Sedinta individuala', '2026-06-17 08:55:00.000000', 'SYSTEM', 'SYSTEM'),
(392, 0, 1, '2026-06-08 09:20:00.000000', '2026-06-19', 50, 13, NULL, '08:00:00', '08:50:00', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, 0, 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Sedinta individuala', '2026-06-19 08:55:00.000000', 'SYSTEM', 'SYSTEM'),
(393, 0, 1, '2026-06-08 09:25:00.000000', '2026-06-22', 50, 13, NULL, '08:00:00', '08:50:00', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, 0, 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Sedinta individuala', '2026-06-22 08:55:00.000000', 'SYSTEM', 'SYSTEM'),
(394, 0, 1, '2026-06-08 09:30:00.000000', '2026-06-24', 50, 13, NULL, '08:00:00', '08:50:00', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, 0, 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Sedinta individuala', '2026-06-24 08:55:00.000000', 'SYSTEM', 'SYSTEM'),
(395, 0, 1, '2026-06-08 09:35:00.000000', '2026-06-26', 50, 13, NULL, '08:00:00', '08:50:00', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, 0, 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Sedinta individuala', '2026-06-26 08:55:00.000000', 'SYSTEM', 'SYSTEM'),
(396, 0, 1, '2026-06-08 09:40:00.000000', '2026-06-29', 50, 13, NULL, '08:00:00', '08:50:00', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, 0, 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Sedinta individuala', '2026-06-29 08:55:00.000000', 'SYSTEM', 'SYSTEM'),
(397, 0, 1, '2026-06-08 09:45:00.000000', '2026-07-01', 50, 13, NULL, '08:00:00', '08:50:00', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 100.00, 0, 6, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Sedinta individuala', '2026-07-01 13:00:00.000000', 'SYSTEM', 'SYSTEM'),
-- Stan Daniela: sesiunile 3-5 din cota de 6 (eval Jun 8) + 1 viitoare + 1 reevaluare
(400, 0, 1, '2026-06-08 13:10:00.000000', '2026-06-17', 50, 2, NULL, '12:00:00', '12:50:00', '235aa647-1c6c-479f-aa26-011539b51b73', 100.00, 0, 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Sedinta individuala', '2026-06-17 12:55:00.000000', 'SYSTEM', 'SYSTEM'),
(401, 0, 1, '2026-06-08 13:15:00.000000', '2026-06-19', 50, 2, NULL, '12:00:00', '12:50:00', '235aa647-1c6c-479f-aa26-011539b51b73', 100.00, 0, 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Sedinta individuala', '2026-06-19 12:55:00.000000', 'SYSTEM', 'SYSTEM'),
(402, 0, 1, '2026-06-08 13:20:00.000000', '2026-06-22', 50, 2, NULL, '12:00:00', '12:50:00', '235aa647-1c6c-479f-aa26-011539b51b73', 100.00, 0, 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Sedinta individuala', '2026-06-22 12:55:00.000000', 'SYSTEM', 'SYSTEM'),
(403, 0, 1, '2026-06-08 13:25:00.000000', '2026-07-01', 50, 2, NULL, '12:00:00', '12:50:00', '235aa647-1c6c-479f-aa26-011539b51b73', 100.00, 0, 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Sedinta individuala', '2026-07-01 13:00:00.000000', 'SYSTEM', 'SYSTEM'),
(430, 1, 0, '2026-06-08 13:30:00.000000', '2026-07-06', 30, 2, NULL, '12:00:00', '12:30:00', '235aa647-1c6c-479f-aa26-011539b51b73', 150.00, 0, 2, 'PROGRAMATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Evaluare - Reevaluare', '2026-06-08 13:30:00.000000', 'SYSTEM', 'SYSTEM'),
-- Popescu Ana-Maria: sesiunile 3-7 din cota de 8 (eval Jun 8) + 1 viitoare + 1 reevaluare
(410, 0, 1, '2026-06-08 14:10:00.000000', '2026-06-17', 50, 1, NULL, '13:00:00', '13:50:00', '9f90d398-f030-4ce4-bfee-033758993378', 100.00, 0, 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Sedinta individuala', '2026-06-17 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(411, 0, 1, '2026-06-08 14:15:00.000000', '2026-06-19', 50, 1, NULL, '13:00:00', '13:50:00', '9f90d398-f030-4ce4-bfee-033758993378', 100.00, 0, 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Sedinta individuala', '2026-06-19 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(412, 0, 1, '2026-06-08 14:20:00.000000', '2026-06-22', 50, 1, NULL, '13:00:00', '13:50:00', '9f90d398-f030-4ce4-bfee-033758993378', 100.00, 0, 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Sedinta individuala', '2026-06-22 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(413, 0, 1, '2026-06-08 14:25:00.000000', '2026-06-24', 50, 1, NULL, '13:00:00', '13:50:00', '9f90d398-f030-4ce4-bfee-033758993378', 100.00, 0, 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Sedinta individuala', '2026-06-24 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(414, 0, 1, '2026-06-08 14:30:00.000000', '2026-06-26', 50, 1, NULL, '13:00:00', '13:50:00', '9f90d398-f030-4ce4-bfee-033758993378', 100.00, 0, 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Sedinta individuala', '2026-06-26 13:55:00.000000', 'SYSTEM', 'SYSTEM'),
(415, 0, 1, '2026-06-08 14:35:00.000000', '2026-07-01', 50, 1, NULL, '13:00:00', '13:50:00', '9f90d398-f030-4ce4-bfee-033758993378', 100.00, 0, 6, 'FINALIZATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Kinetoterapie - Sedinta individuala', '2026-07-01 13:00:00.000000', 'SYSTEM', 'SYSTEM'),
(431, 1, 0, '2026-06-08 14:40:00.000000', '2026-07-06', 30, 1, NULL, '13:00:00', '13:30:00', '9f90d398-f030-4ce4-bfee-033758993378', 150.00, 0, 2, 'PROGRAMATA', '496b7af4-d378-418d-8d5f-5647ed31d11d', 'Evaluare - Reevaluare', '2026-06-08 14:40:00.000000', 'SYSTEM', 'SYSTEM'),
-- Reevaluare programata Dumitrescu Ana - Jul 6
(432, 1, 0, '2026-06-08 09:50:00.000000', '2026-07-06', 30, 13, NULL, '08:00:00', '08:30:00', 'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d', 150.00, 0, 2, 'PROGRAMATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Evaluare - Reevaluare', '2026-06-08 09:50:00.000000', 'SYSTEM', 'SYSTEM'),
-- Maria Ionescu: sesiunea 5/5 din cota reevaluarii Jun 17 (Jul 15)
(420, 0, 1, '2026-06-17 10:00:00.000000', '2026-07-15', 50, 13, NULL, '10:00:00', '10:50:00', 'de85c053-a583-4219-8fe9-5afd66c0e812', 110.00, 0, 8, 'FINALIZATA', 'd407ab1c-7554-456d-9726-3f5571fa8fd0', 'Kinetoterapie - Gimnastica profilactica', '2026-07-15 10:55:00.000000', 'SYSTEM', 'SYSTEM');

-- Dump completed on 2026-06-25


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



-- USER CORRECTIONS - June 25, 2026
-- Delete canceled appointment 63 to remove Popescu Ana-Maria from Alexandru's recent patients list
DELETE FROM programari WHERE id = 63;

