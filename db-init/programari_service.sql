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
INSERT INTO `evaluari` VALUES (1,'2026-02-28 19:00:42.292059','2026-02-27','scolioza','Prezinta dureri la spate nu stiu','2733282b-1ee8-4afb-827a-aa0ff0b98f61',1,3,3,'496b7af4-d378-418d-8d5f-5647ed31d11d','INITIALA','2026-02-28 19:00:42.292059'),(2,'2026-03-15 17:30:46.429070','2026-03-03','scolioza','','2733282b-1ee8-4afb-827a-aa0ff0b98f61',7,3,3,'496b7af4-d378-418d-8d5f-5647ed31d11d','REEVALUARE','2026-03-16 18:23:03.286194'),(3,'2026-03-17 16:51:33.333250','2026-03-17','dorsalgie','','072ee168-7759-4613-96e1-226f0d7277ea',9,5,3,'496b7af4-d378-418d-8d5f-5647ed31d11d','INITIALA','2026-03-17 16:51:33.333250'),(4,'2026-03-05 09:35:00.000000','2026-03-05','Hernie de disc L4-L5','Postură asimetrică la nivelul umerilor și bazinului, cu rotație toracală vizibilă. Mobilitate redusă în flexia laterală stângă. Pacientul raportează disconfort ocazional în zona lombară la efort prelungit.','77d841ba-ba92-4d98-a6bd-873c58fbc440',10,10,6,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','INITIALA','2026-03-05 09:35:00.000000'),(5,'2026-03-10 09:35:00.000000','2026-03-10','Cifoscolioză','Asimetrie scapulară evidentă. Tensiune crescută pe musculatura paravertebrală dreaptă. Necesită corectare posturală activă și stretching.','e61befe0-0110-45b7-9a31-8f433b113909',12,5,6,'f90f7761-c2ee-41f5-92fb-96c47b6567f5','INITIALA','2026-03-10 09:35:00.000000'),(6,'2026-03-10 09:35:00.000000','2026-02-16','Lombosciatica dreapta','Pacienta prezinta durere ascutita la flexia trunchiului. Testul Lasegue pozitiv la 45 grade. Deficit usor de forta pe flexia dorsala.','31d03fc1-1800-4a52-9817-0c6b21d78e14',17,10,6,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','INITIALA','2026-03-10 09:35:00.000000'),(7,'2026-03-12 08:35:00.000000','2026-03-12','Tendinită ahiliană','Durere acută la palpare și la flexia dorsală a gleznei stângi. Mers antalgic ușor șchiopătat observat la intrarea în cabinet.','6da1727a-5388-4bfb-b845-d96485c61498',20,8,6,'d407ab1c-7554-456d-9726-3f5571fa8fd0','INITIALA','2026-03-12 08:35:00.000000'),(8,'2026-03-18 12:35:00.000000','2026-03-18','Sindrom patelofemural','Crepitus palpabil la nivelul genunchiului drept în timpul genuflexiunilor. Slăbiciune marcată a vastului medial (VMO).','6c400be7-84b0-40f9-8854-b90e6c8358d9',22,10,6,'cb3ac7f5-4730-44cf-a053-d17f451f4b02','INITIALA','2026-03-18 12:35:00.000000'),(9,'2026-03-20 10:35:00.000000','2026-03-20','Fasciită plantară','Durere matinală severă declarată la primii pași. Retracție semnificativă pe lanțul fascial posterior. Fascia de la piciorul drept îngroșată la palpare.','487da990-f8ed-437f-b5d9-e9e2f5aef199',24,6,6,'69f7e2ae-39c0-4506-8558-d782493821a0','INITIALA','2026-03-20 10:35:00.000000'),(10,'2026-03-23 11:35:00.000000','2026-03-23','Cervicalgie miotensivă','Contractură marcată a mușchiului trapez bilateral. Tensiune ridicată pe musculatura suboccipitală, cel mai probabil indusă de lucrul prelungit la birou cu postură incorectă.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',25,5,7,'51abc54c-0c2f-4f0a-aead-bdaa9627661e','INITIALA','2026-03-23 11:35:00.000000'),(11,'2026-03-19 14:30:00.000000','2026-03-19','Lombosciatică stângă','Durere lombară cu iradiere pe membrul inferior stâng. Test Lasegue pozitiv la 50 de grade. Recomand începerea exercițiilor de decompresie și tonifiere progresivă, folosind rezistență elastică.','5f89ace3-454d-486a-accd-986caa20d84a',37,10,6,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','INITIALA','2026-03-19 14:30:00.000000'),(12,'2026-02-16 09:40:00.000000','2026-02-16','Entorsă gleznă grad II','Edem moderat la nivelul maleolei externe. Mobilitate redusă în flexie dorsală. Pacienta a început recuperarea după 3 săptămâni de imobilizare.','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',38,12,6,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','INITIALA','2026-02-16 09:40:00.000000'),(13,'2026-02-19 14:40:00.000000','2026-02-19','Cifoză dorsală','Tensiune pe musculatura pectorală. Spate rotund vizibil în repaus. Pacientul nu are istoric sportiv și lucrează foarte mult la birou.','00179244-a86e-4ade-8e22-3d70af6de57d',51,7,6,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','INITIALA','2026-03-25 18:31:04.488551'),(14,'2026-03-25 18:42:57.750882','2026-03-25','Cifoză dorsală','Pacientul este cooperant și a respectat parțial recomandările de exerciții pentru acasă. Se menține tendința de poziție cifotică în perioadele prelungite de lucru la birou.','00179244-a86e-4ade-8e22-3d70af6de57d',66,5,6,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','REEVALUARE','2026-03-25 18:42:57.750882');
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
INSERT INTO `evolutii` VALUES (1,'2026-02-28 19:01:02.181851','abcdefghijklmn','2733282b-1ee8-4afb-827a-aa0ff0b98f61','496b7af4-d378-418d-8d5f-5647ed31d11d','2026-02-28 19:01:02.181851'),(2,'2026-03-16 18:24:46.152991','abc','2733282b-1ee8-4afb-827a-aa0ff0b98f61','496b7af4-d378-418d-8d5f-5647ed31d11d','2026-03-16 18:24:46.152991'),(3,'2026-03-20 11:00:00.000000','A efectuat exercițiile corect, dar are dificultăți la menținerea posturii. Necesită mai multă atenție pe controlul trunchiului și respirație diafragmatică în timpul efortului.','77d841ba-ba92-4d98-a6bd-873c58fbc440','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-03-20 11:00:00.000000'),(4,'2026-03-20 15:00:00.000000','Toleranță crescută la exercițiile cu rezistență elastică. Am progresat la lucrul cu greutăți mici. Durerea iradiantă pe picior a scăzut semnificativ.','5f89ace3-454d-486a-accd-986caa20d84a','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-19 15:00:00.000000'),(5,'2026-03-02 10:00:00.000000','Mobilitatea lombara s-a imbunatatit usor dupa exercitiile de decomprimare. Vom insista pe stretching activ.','31d03fc1-1800-4a52-9817-0c6b21d78e14','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-17 11:00:00.000000'),(6,'2026-03-19 10:00:00.000000','Am început protocolul de exerciții excentrice. Pacientul tolerează bine sarcina pe tendon, durerea raportată în timpul execuției este minimă.','6da1727a-5388-4bfb-b845-d96485c61498','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-03-19 10:00:00.000000'),(7,'2026-03-18 12:40:00.000000','Pacient deschis, înțelege importanța activării gluteilor. Am stabilit exerciții simple de făcut și acasă zilnic.','6c400be7-84b0-40f9-8854-b90e6c8358d9','cb3ac7f5-4730-44cf-a053-d17f451f4b02','2026-03-18 12:40:00.000000'),(8,'2026-03-16 10:00:00.000000','Pacienta a executat corect exercitiile de stabilizare lombara (Bird-Dog). Am crescut usor rezistenta. Progres vizibil pe mobilitate.','31d03fc1-1800-4a52-9817-0c6b21d78e14','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-24 10:05:00.000000'),(9,'2026-03-23 10:00:00.000000','Aproape de recuperare completa a mobilitatii. Pregatim planul de intretinere pe termen lung pentru acasa.','31d03fc1-1800-4a52-9817-0c6b21d78e14','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-23 10:00:00.000000'),(10,'2026-03-03 11:00:00.000000','Edemul a dispărut complet. Mersul pe plan drept s-a îmbunătățit, dar încă are ușoară instabilitate la coborârea scărilor.','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-03 11:00:00.000000'),(11,'2026-03-17 11:00:00.000000','Am introdus exerciții pe placa de propriocepție. Pacienta reacționează foarte bine la stimulii de dezechilibru ușor.','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-17 11:00:00.000000'),(12,'2026-03-06 16:00:00.000000','Înțelege greu activarea romboizilor. Compensăm prin exerciții ghidate tactil. Toleranță scăzută la efort.','00179244-a86e-4ade-8e22-3d70af6de57d','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-06 16:00:00.000000'),(13,'2026-03-20 16:00:00.000000','Postura începe să se corecteze. Pacientul relatează că resimte oboseală musculară mai puțin la birou. Continuăm pe tonifiere.','00179244-a86e-4ade-8e22-3d70af6de57d','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-20 16:00:00.000000');
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
  PRIMARY KEY (`id`),
  KEY `idx_prog_terapeut_data_status` (`terapeut_keycloak_id`,`data`,`status`),
  KEY `idx_prog_pacient_data` (`pacient_keycloak_id`,`data`),
  KEY `idx_prog_status_data` (`status`,`data`),
  KEY `idx_prog_pacient_status_data` (`pacient_keycloak_id`,`status`,`data`,`ora_inceput`),
  KEY `idx_prog_stats` (`locatie_id`,`data`),
  KEY `idx_prog_overlap` (`terapeut_keycloak_id`,`data`,`ora_inceput`,`ora_sfarsit`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `programari`
--

LOCK TABLES `programari` WRITE;
/*!40000 ALTER TABLE `programari` DISABLE KEYS */;
INSERT INTO `programari` VALUES (1,_binary '',_binary '','2026-02-28 18:54:07.898168','2026-02-27',30,1,NULL,'09:00:00.000000','09:30:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',100.00,_binary '',1,'FINALIZATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Evaluare Initiala - Evaluare Initiala','2026-02-28 19:11:49.361497'),(2,_binary '\0',_binary '','2026-03-02 15:10:52.474013','2026-02-27',45,1,NULL,'11:30:00.000000','12:15:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',150.00,_binary '\0',6,'FINALIZATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Kinetoterapie - Masaj','2026-03-16 17:11:20.676409'),(3,_binary '\0',_binary '','2026-03-02 17:19:06.000000','2026-02-28',45,1,NULL,'09:00:00.000000','09:45:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',150.00,_binary '\0',6,'FINALIZATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Kinetoterapie - Masaj','2026-03-16 17:11:36.371410'),(4,_binary '\0',_binary '','2026-03-02 17:19:06.000000','2026-02-28',45,1,NULL,'10:00:00.000000','10:45:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',150.00,_binary '\0',6,'FINALIZATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Kinetoterapie - Masaj','2026-03-16 17:11:45.813602'),(5,_binary '',_binary '\0','2026-03-02 15:28:41.894072','2026-03-03',30,1,'ANULAT_DE_PACIENT','09:00:00.000000','09:30:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',100.00,_binary '\0',2,'ANULATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Evaluare - Reevaluare','2026-03-02 15:31:11.757415'),(6,_binary '',_binary '\0','2026-03-02 15:36:13.728994','2026-03-03',30,1,'ANULAT_DE_PACIENT','11:00:00.000000','11:30:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',100.00,_binary '\0',2,'ANULATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Evaluare - Reevaluare','2026-03-02 15:49:05.252698'),(7,_binary '',_binary '','2026-03-02 16:07:41.858404','2026-03-03',30,1,NULL,'09:40:00.000000','10:10:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',100.00,_binary '\0',2,'FINALIZATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Evaluare - Reevaluare','2026-03-16 17:13:40.681471'),(8,_binary '\0',_binary '\0','2026-03-16 18:13:25.786179','2026-03-17',45,1,'ANULAT_DE_TERAPEUT','09:55:00.000000','10:40:00.000000','2733282b-1ee8-4afb-827a-aa0ff0b98f61',150.00,_binary '\0',6,'ANULATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Kinetoterapie - Masaj','2026-03-16 18:22:12.576124'),(9,_binary '',_binary '\0','2026-03-16 18:28:57.399746','2026-03-17',30,1,NULL,'09:40:00.000000','10:10:00.000000','072ee168-7759-4613-96e1-226f0d7277ea',100.00,_binary '',1,'FINALIZATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Evaluare - Evaluare Initiala','2026-03-17 16:51:33.393048'),(10,_binary '',_binary '','2026-03-01 10:10:00.000000','2026-03-05',30,7,NULL,'09:00:00.000000','09:30:00.000000','77d841ba-ba92-4d98-a6bd-873c58fbc440',150.00,_binary '',1,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Initiala','2026-03-05 09:30:00.000000'),(11,_binary '\0',_binary '','2026-03-15 10:10:00.000000','2026-03-20',50,2,NULL,'10:00:00.000000','10:50:00.000000','77d841ba-ba92-4d98-a6bd-873c58fbc440',100.00,_binary '',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-03-20 10:50:00.000000'),(12,_binary '',_binary '','2026-03-05 10:10:00.000000','2026-03-10',30,1,NULL,'09:00:00.000000','09:30:00.000000','e61befe0-0110-45b7-9a31-8f433b113909',150.00,_binary '',1,'FINALIZATA','f90f7761-c2ee-41f5-92fb-96c47b6567f5','Evaluare - Evaluare Initiala','2026-03-10 09:30:00.000000'),(13,_binary '\0',_binary '\0','2026-03-20 10:00:00.000000','2026-03-26',50,1,NULL,'09:00:00.000000','09:50:00.000000','e61befe0-0110-45b7-9a31-8f433b113909',100.00,_binary '\0',6,'PROGRAMATA','f90f7761-c2ee-41f5-92fb-96c47b6567f5','Kinetoterapie - Ședință individuală','2026-03-20 10:00:00.000000'),(14,_binary '\0',_binary '','2026-03-10 12:10:00.000000','2026-03-20',50,8,NULL,'14:00:00.000000','14:50:00.000000','5f89ace3-454d-486a-accd-986caa20d84a',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-19 14:50:00.000000'),(15,_binary '\0',_binary '\0','2026-03-19 15:00:00.000000','2026-03-27',50,8,'ANULAT_DE_PACIENT','14:00:00.000000','14:50:00.000000','5f89ace3-454d-486a-accd-986caa20d84a',100.00,_binary '\0',6,'ANULATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-24 18:21:06.526258'),(16,_binary '',_binary '\0','2026-03-14 09:10:00.000000','2026-03-30',30,13,NULL,'08:00:00.000000','08:30:00.000000','ac0ef5eb-34df-4a8c-8f4a-e879428aed2d',150.00,_binary '',1,'PROGRAMATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Evaluare - Evaluare Initiala','2026-03-14 09:10:00.000000'),(17,_binary '',_binary '','2026-03-08 10:10:00.000000','2026-02-16',30,7,NULL,'09:00:00.000000','09:30:00.000000','31d03fc1-1800-4a52-9817-0c6b21d78e14',150.00,_binary '',1,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Initiala','2026-03-10 09:30:00.000000'),(18,_binary '\0',_binary '','2026-03-10 09:40:00.000000','2026-02-17',50,7,NULL,'09:00:00.000000','09:50:00.000000','31d03fc1-1800-4a52-9817-0c6b21d78e14',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Sedinta individuala','2026-03-17 10:50:00.000000'),(19,_binary '\0',_binary '','2026-03-17 11:00:00.000000','2026-02-23',50,7,NULL,'09:00:00.000000','09:50:00.000000','31d03fc1-1800-4a52-9817-0c6b21d78e14',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Sedinta individuala','2026-03-17 11:00:00.000000'),(20,_binary '',_binary '','2026-03-10 11:10:00.000000','2026-03-12',30,13,NULL,'08:00:00.000000','08:30:00.000000','6da1727a-5388-4bfb-b845-d96485c61498',150.00,_binary '',1,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Evaluare - Evaluare Inițială','2026-03-12 08:30:00.000000'),(21,_binary '\0',_binary '','2026-03-12 08:40:00.000000','2026-03-19',50,13,NULL,'09:00:00.000000','09:50:00.000000','6da1727a-5388-4bfb-b845-d96485c61498',100.00,_binary '\0',6,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Ședință individuală','2026-03-19 09:50:00.000000'),(22,_binary '',_binary '','2026-03-15 12:10:00.000000','2026-03-18',30,10,NULL,'12:00:00.000000','12:30:00.000000','6c400be7-84b0-40f9-8854-b90e6c8358d9',150.00,_binary '',1,'FINALIZATA','cb3ac7f5-4730-44cf-a053-d17f451f4b02','Evaluare - Evaluare Inițială','2026-03-18 12:30:00.000000'),(23,_binary '\0',_binary '\0','2026-03-18 12:40:00.000000','2026-03-26',50,10,NULL,'13:00:00.000000','13:50:00.000000','6c400be7-84b0-40f9-8854-b90e6c8358d9',100.00,_binary '\0',6,'PROGRAMATA','cb3ac7f5-4730-44cf-a053-d17f451f4b02','Kinetoterapie - Ședință individuală','2026-03-18 12:40:00.000000'),(24,_binary '',_binary '','2026-03-18 14:10:00.000000','2026-03-20',30,9,NULL,'10:00:00.000000','10:30:00.000000','487da990-f8ed-437f-b5d9-e9e2f5aef199',150.00,_binary '',1,'FINALIZATA','69f7e2ae-39c0-4506-8558-d782493821a0','Evaluare - Evaluare Inițială','2026-03-20 10:30:00.000000'),(25,_binary '',_binary '','2026-03-21 09:10:00.000000','2026-03-23',30,2,NULL,'11:00:00.000000','11:30:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',150.00,_binary '',1,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Evaluare - Evaluare Inițială','2026-03-23 11:30:00.000000'),(26,_binary '',_binary '\0','2026-03-24 10:10:00.000000','2026-03-27',30,1,NULL,'09:00:00.000000','09:30:00.000000','235aa647-1c6c-479f-aa26-011539b51b73',150.00,_binary '',1,'PROGRAMATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Evaluare - Evaluare Inițială','2026-03-24 10:10:00.000000'),(36,_binary '',_binary '\0','2026-03-24 10:00:00.000000','2026-03-30',30,7,NULL,'09:00:00.000000','09:30:00.000000','31d03fc1-1800-4a52-9817-0c6b21d78e14',100.00,_binary '\0',2,'PROGRAMATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Reevaluare','2026-03-24 10:00:00.000000'),(37,_binary '',_binary '\0','2026-03-24 18:21:11.785732','2026-03-19',30,8,NULL,'14:00:00.000000','14:30:00.000000','5f89ace3-454d-486a-accd-986caa20d84a',150.00,_binary '\0',1,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Initiala','2026-03-24 20:27:34.334925'),(38,_binary '',_binary '','2026-02-15 11:00:00.000000','2026-02-16',30,7,NULL,'10:00:00.000000','10:30:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',150.00,_binary '',1,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Inițială','2026-02-16 09:30:00.000000'),(39,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-02-17',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-02-17 10:50:00.000000'),(41,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-02-24',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-02-24 10:50:00.000000'),(42,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-03-02',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-02 09:50:00.000000'),(43,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-03-03',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-03 10:50:00.000000'),(44,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-03-09',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-09 09:50:00.000000'),(45,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-03-10',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-10 10:50:00.000000'),(46,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-03-16',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-16 09:50:00.000000'),(47,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-03-17',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-17 10:50:00.000000'),(48,_binary '\0',_binary '','2026-02-16 10:00:00.000000','2026-03-23',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-23 09:50:00.000000'),(49,_binary '\0',_binary '\0','2026-02-16 10:00:00.000000','2026-03-24',50,7,'NEPREZENTARE','09:00:00.000000','09:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'ANULATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-25 19:11:21.000000'),(50,_binary '\0',_binary '\0','2026-02-16 10:00:00.000000','2026-03-30',50,7,'ANULAT_DE_PACIENT','10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'ANULATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-25 18:26:31.037939'),(51,_binary '',_binary '','2026-02-18 11:00:00.000000','2026-02-19',30,8,NULL,'15:00:00.000000','15:30:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',150.00,_binary '',1,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Inițială','2026-02-19 14:30:00.000000'),(52,_binary '\0',_binary '','2026-02-19 15:00:00.000000','2026-02-20',50,8,NULL,'15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-02-20 15:50:00.000000'),(55,_binary '\0',_binary '','2026-02-19 15:00:00.000000','2026-03-05',50,8,NULL,'15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-05 14:50:00.000000'),(56,_binary '\0',_binary '','2026-02-19 15:00:00.000000','2026-03-06',50,8,NULL,'15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-06 15:50:00.000000'),(57,_binary '\0',_binary '','2026-02-19 15:00:00.000000','2026-03-12',50,8,NULL,'15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-12 14:50:00.000000'),(58,_binary '\0',_binary '','2026-02-19 15:00:00.000000','2026-03-13',50,8,NULL,'15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-13 15:50:00.000000'),(59,_binary '\0',_binary '','2026-02-19 15:00:00.000000','2026-03-19',50,8,NULL,'15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-19 14:50:00.000000'),(60,_binary '\0',_binary '','2026-02-19 15:00:00.000000','2026-03-20',50,8,NULL,'15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-20 15:50:00.000000'),(61,_binary '\0',_binary '\0','2026-02-19 15:00:00.000000','2026-03-26',50,8,'ANULAT_DE_PACIENT','15:00:00.000000','15:50:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',6,'ANULATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-25 18:30:11.538734'),(62,_binary '\0',_binary '\0','2026-03-25 17:37:53.860261','2026-03-28',30,7,'ANULAT_DE_PACIENT','09:00:00.000000','09:30:00.000000','9f90d398-f030-4ce4-bfee-033758993378',150.00,_binary '',1,'ANULATA','496b7af4-d378-418d-8d5f-5647ed31d11d','Evaluare - Evaluare Initiala','2026-03-25 17:38:18.223909'),(63,_binary '\0',_binary '\0','2026-03-25 17:38:31.418367','2026-03-30',30,7,'ANULAT_DE_PACIENT','11:00:00.000000','11:30:00.000000','9f90d398-f030-4ce4-bfee-033758993378',150.00,_binary '',1,'ANULATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Initiala','2026-03-25 17:44:47.098658'),(64,_binary '\0',_binary '\0','2026-03-25 17:44:53.215191','2026-03-30',30,7,NULL,'11:40:00.000000','12:10:00.000000','9f90d398-f030-4ce4-bfee-033758993378',150.00,_binary '',1,'PROGRAMATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Initiala','2026-03-25 17:44:53.215191'),(65,_binary '\0',_binary '\0','2026-03-25 18:26:58.825338','2026-03-30',50,7,NULL,'10:00:00.000000','10:50:00.000000','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',100.00,_binary '\0',6,'PROGRAMATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Ședință individuală','2026-03-25 18:26:58.826027'),(66,_binary '',_binary '\0','2026-03-25 18:32:50.676753','2026-03-25',30,8,NULL,'14:00:00.000000','14:30:00.000000','00179244-a86e-4ade-8e22-3d70af6de57d',100.00,_binary '\0',2,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Reevaluare','2026-03-25 18:42:57.843328');
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
INSERT INTO `relatie_pacient_terapeut` VALUES (1,_binary '','2026-02-28 18:58:03.495685','2026-02-27',NULL,'2733282b-1ee8-4afb-827a-aa0ff0b98f61','496b7af4-d378-418d-8d5f-5647ed31d11d','2026-02-28 18:58:03.495685'),(2,_binary '','2026-03-17 16:19:38.392485','2026-03-17',NULL,'072ee168-7759-4613-96e1-226f0d7277ea','496b7af4-d378-418d-8d5f-5647ed31d11d','2026-03-17 16:19:38.393680'),(3,_binary '\0','2026-03-01 10:00:00.000000','2026-03-01','2026-03-15','77d841ba-ba92-4d98-a6bd-873c58fbc440','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-15 10:00:00.000000'),(4,_binary '','2026-03-15 10:05:00.000000','2026-03-15',NULL,'77d841ba-ba92-4d98-a6bd-873c58fbc440','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-03-15 10:05:00.000000'),(5,_binary '\0','2026-03-02 09:00:00.000000','2026-03-02','2026-03-18','5ff54c13-8ec1-40b3-b147-bbf2ad48fb86','cb3ac7f5-4730-44cf-a053-d17f451f4b02','2026-03-18 09:00:00.000000'),(6,_binary '','2026-03-18 09:10:00.000000','2026-03-18',NULL,'5ff54c13-8ec1-40b3-b147-bbf2ad48fb86','69f7e2ae-39c0-4506-8558-d782493821a0','2026-03-18 09:10:00.000000'),(7,_binary '','2026-03-05 10:00:00.000000','2026-03-05',NULL,'e61befe0-0110-45b7-9a31-8f433b113909','f90f7761-c2ee-41f5-92fb-96c47b6567f5','2026-03-05 10:00:00.000000'),(8,_binary '','2026-03-06 11:00:00.000000','2026-03-06',NULL,'de85c053-a583-4219-8fe9-5afd66c0e812','69f7e2ae-39c0-4506-8558-d782493821a0','2026-03-06 11:00:00.000000'),(9,_binary '','2026-03-10 12:00:00.000000','2026-03-10',NULL,'5f89ace3-454d-486a-accd-986caa20d84a','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-10 12:00:00.000000'),(10,_binary '','2026-03-12 14:00:00.000000','2026-03-12',NULL,'8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-03-12 14:00:00.000000'),(11,_binary '','2026-03-14 09:00:00.000000','2026-03-14',NULL,'ac0ef5eb-34df-4a8c-8f4a-e879428aed2d','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-03-14 09:00:00.000000'),(12,_binary '','2026-03-08 10:00:00.000000','2026-03-08',NULL,'31d03fc1-1800-4a52-9817-0c6b21d78e14','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-08 10:00:00.000000'),(13,_binary '','2026-03-10 11:00:00.000000','2026-03-10',NULL,'6da1727a-5388-4bfb-b845-d96485c61498','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-03-10 11:00:00.000000'),(14,_binary '','2026-03-15 12:00:00.000000','2026-03-15',NULL,'6c400be7-84b0-40f9-8854-b90e6c8358d9','cb3ac7f5-4730-44cf-a053-d17f451f4b02','2026-03-15 12:00:00.000000'),(15,_binary '','2026-03-18 14:00:00.000000','2026-03-18',NULL,'487da990-f8ed-437f-b5d9-e9e2f5aef199','69f7e2ae-39c0-4506-8558-d782493821a0','2026-03-18 14:00:00.000000'),(16,_binary '','2026-03-21 09:00:00.000000','2026-03-21',NULL,'a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-03-21 09:00:00.000000'),(17,_binary '','2026-03-24 10:00:00.000000','2026-03-24',NULL,'235aa647-1c6c-479f-aa26-011539b51b73','496b7af4-d378-418d-8d5f-5647ed31d11d','2026-03-24 10:00:00.000000'),(18,_binary '','2026-02-15 10:00:00.000000','2026-02-15',NULL,'b6f3d53a-f2d3-4e2b-b531-d1463a88ef70','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-02-15 10:00:00.000000'),(19,_binary '','2026-02-18 10:00:00.000000','2026-02-18',NULL,'00179244-a86e-4ade-8e22-3d70af6de57d','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-02-18 10:00:00.000000');
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

-- Dump completed on 2026-05-13 13:39:54
