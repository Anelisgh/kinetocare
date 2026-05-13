-- MySQL dump 10.13  Distrib 8.0.35, for Win64 (x86_64)
--
-- Host: localhost    Database: pacienti_service
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
-- Current Database: `pacienti_service`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `pacienti_service` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `pacienti_service`;

--
-- Table structure for table `jurnal_pacient`
--

DROP TABLE IF EXISTS `jurnal_pacient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jurnal_pacient` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comentarii` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) NOT NULL,
  `data` date NOT NULL,
  `dificultate_exercitii` int NOT NULL,
  `nivel_durere` int NOT NULL,
  `nivel_oboseala` int NOT NULL,
  `pacient_id` bigint NOT NULL,
  `programare_id` bigint NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_jurnal_pacient` (`pacient_id`),
  KEY `idx_jurnal_programare` (`programare_id`),
  KEY `idx_jurnal_data` (`data`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jurnal_pacient`
--

LOCK TABLES `jurnal_pacient` WRITE;
/*!40000 ALTER TABLE `jurnal_pacient` DISABLE KEYS */;
INSERT INTO `jurnal_pacient` VALUES (1,'klmnop','2026-02-28 19:11:48.507655','2026-02-27',3,2,4,1,1,'2026-02-28 19:11:48.508214'),(2,'','2026-03-16 17:11:20.139105','2026-02-27',4,3,5,1,2,'2026-03-16 17:11:20.139105'),(3,'','2026-03-16 17:11:36.150387','2026-02-28',1,2,3,1,3,'2026-03-16 17:11:36.150387'),(4,'','2026-03-16 17:11:45.781747','2026-02-28',1,1,1,1,4,'2026-03-16 17:11:45.781747'),(5,'','2026-03-16 17:13:39.562864','2026-03-03',3,3,3,1,7,'2026-03-16 17:13:39.563422'),(6,'Am simțit o ușoară jenă după evaluare, dar a trecut repede.','2026-03-05 18:00:00.000000','2026-03-05',2,4,3,9,10,'2026-03-05 18:00:00.000000'),(7,'Exercițiile par mai ușoare acum cu noul terapeut, mă simt mai relaxat.','2026-03-20 19:30:00.000000','2026-03-20',3,2,4,9,11,'2026-03-20 19:30:00.000000'),(8,'Mă dor puțin umerii de la stretching.','2026-03-10 16:00:00.000000','2026-03-10',4,3,2,7,12,'2026-03-10 16:00:00.000000'),(9,'M-am simțit excelent la antrenament azi.','2026-03-20 17:00:00.000000','2026-03-20',2,1,5,10,14,'2026-03-19 17:00:00.000000'),(10,'Am fost putin tensionata la inceput, dar mi-a fost explicat clar. Durerea a fost prezenta la anumite miscari.','2026-03-10 18:00:00.000000','2026-02-16',2,6,3,14,17,'2026-03-10 18:00:00.000000'),(11,'Simt o usurare pe picior, sper sa mearga tot asa de bine.','2026-03-17 19:30:00.000000','2026-02-17',4,4,4,14,18,'2026-03-17 19:30:00.000000'),(12,'Exercițiile mi s-au părut destul de simple, nu m-am obosit deloc azi.','2026-03-12 16:00:00.000000','2026-03-12',1,5,2,15,20,'2026-03-12 16:00:00.000000'),(13,'Am simțit un pic de arsură la gambă în timpul repetărilor, dar a trecut după ședință.','2026-03-19 17:00:00.000000','2026-03-19',5,3,5,15,21,'2026-03-19 17:00:00.000000'),(14,'Genunchiul mai trosnește, dar par să am un control mai bun când cobor scările.','2026-03-18 20:00:00.000000','2026-03-18',3,4,3,16,22,'2026-03-18 20:00:00.000000'),(15,'Doare destul de tare când calc dimineața, dar masajul de la finalul ședinței a fost foarte relaxant.','2026-03-20 18:30:00.000000','2026-03-20',2,7,3,17,24,'2026-03-20 18:30:00.000000'),(16,'Am plecat de la cabinet fără tensiunea obișnuită din umeri. Sper să mă țină senzația asta.','2026-03-23 15:00:00.000000','2026-03-23',2,5,4,18,25,'2026-03-23 15:00:00.000000'),(19,'Ma simt mai bine dupa exercitiile de ieri, muschii parca sunt mai relaxati.','2026-02-23 16:00:00.000000','2026-02-23',3,4,3,14,19,'2026-02-23 16:00:00.000000'),(28,'Glezna încă mă doare puțin când încerc să mișc prea mult.','2026-02-16 16:00:00.000000','2026-02-16',3,5,2,20,38,'2026-02-16 16:00:00.000000'),(29,'','2026-02-17 16:00:00.000000','2026-02-17',3,4,3,20,39,'2026-02-17 16:00:00.000000'),(31,'','2026-02-24 16:00:00.000000','2026-02-24',3,3,3,20,41,'2026-02-24 16:00:00.000000'),(32,'','2026-03-02 16:00:00.000000','2026-03-02',3,3,2,20,42,'2026-03-02 16:00:00.000000'),(33,'Nu mă mai doare deloc când merg normal, doar la coborât scările.','2026-03-03 16:00:00.000000','2026-03-03',3,2,2,20,43,'2026-03-03 16:00:00.000000'),(34,'','2026-03-09 16:00:00.000000','2026-03-09',4,2,3,20,44,'2026-03-09 16:00:00.000000'),(35,'','2026-03-10 16:00:00.000000','2026-03-10',4,1,3,20,45,'2026-03-10 16:00:00.000000'),(36,'Exercițiile pe bila aia mică au fost destul de grele, dar simt că mă ajută la echilibru.','2026-03-16 16:00:00.000000','2026-03-16',5,1,4,20,46,'2026-03-16 16:00:00.000000'),(37,'','2026-03-17 16:00:00.000000','2026-03-17',4,1,3,20,47,'2026-03-17 16:00:00.000000'),(38,'M-am simțit excelent azi la antrenament.','2026-03-23 16:00:00.000000','2026-03-23',3,0,2,20,48,'2026-03-23 16:00:00.000000'),(40,'M-a apucat o febră musculară destul de urâtă după prima ședință.','2026-02-19 21:00:00.000000','2026-02-19',4,5,5,21,51,'2026-02-19 21:00:00.000000'),(41,'','2026-02-20 21:00:00.000000','2026-02-20',4,4,4,21,52,'2026-02-20 21:00:00.000000'),(44,'','2026-03-05 21:00:00.000000','2026-03-05',4,2,4,21,55,'2026-03-05 21:00:00.000000'),(45,'Încep să simt cum stau mai drept când sunt la birou.','2026-03-06 21:00:00.000000','2026-03-06',3,2,3,21,56,'2026-03-06 21:00:00.000000'),(46,'','2026-03-12 21:00:00.000000','2026-03-12',3,2,3,21,57,'2026-03-12 21:00:00.000000'),(47,'','2026-03-13 21:00:00.000000','2026-03-13',3,1,3,21,58,'2026-03-13 21:00:00.000000'),(48,'S-a văzut clar diferența la exercițiile cu banda.','2026-03-19 21:00:00.000000','2026-03-19',4,1,4,21,59,'2026-03-19 21:00:00.000000'),(49,'','2026-03-20 21:00:00.000000','2026-03-20',3,1,3,21,60,'2026-03-20 21:00:00.000000');
/*!40000 ALTER TABLE `jurnal_pacient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pacienti`
--

DROP TABLE IF EXISTS `pacienti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pacienti` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `cnp` varchar(13) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `data_nasterii` date DEFAULT NULL,
  `detalii_sport` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `face_sport` enum('DA','NU') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `keycloak_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `locatie_preferata_id` bigint DEFAULT NULL,
  `oras_preferat` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `terapeut_keycloak_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pacienti_keycloak_id` (`keycloak_id`),
  UNIQUE KEY `uk_pacienti_cnp` (`cnp`),
  KEY `idx_pacienti_terapeut` (`terapeut_keycloak_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pacienti`
--

LOCK TABLES `pacienti` WRITE;
/*!40000 ALTER TABLE `pacienti` DISABLE KEYS */;
INSERT INTO `pacienti` VALUES (1,_binary '','1770126282211','2026-02-28 12:41:16.226481','1999-03-29',NULL,'NU','2733282b-1ee8-4afb-827a-aa0ff0b98f61',1,'București','496b7af4-d378-418d-8d5f-5647ed31d11d','2026-02-28 18:23:42.862970'),(3,_binary '','1770126282200','2026-03-16 18:17:15.688803','2000-03-25',NULL,'NU','072ee168-7759-4613-96e1-226f0d7277ea',1,'București','496b7af4-d378-418d-8d5f-5647ed31d11d','2026-03-16 18:19:00.644016'),(7,_binary '','1960523401234','2026-03-24 15:11:33.546331','2010-05-03','Fotbal de 2 ori pe saptamana cu prietenii','DA','e61befe0-0110-45b7-9a31-8f433b113909',1,'București','f90f7761-c2ee-41f5-92fb-96c47b6567f5','2026-03-24 19:19:48.000000'),(8,_binary '','2950817456789','2026-03-24 15:16:26.262250','2019-10-28',NULL,'NU','de85c053-a583-4219-8fe9-5afd66c0e812',9,'Cluj-Napoca','69f7e2ae-39c0-4506-8558-d782493821a0','2026-03-24 19:19:48.000000'),(9,_binary '','2860405123456','2026-03-24 15:26:02.691078','1985-09-11',NULL,'NU','77d841ba-ba92-4d98-a6bd-873c58fbc440',2,'București','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-03-24 19:19:48.000000'),(10,_binary '','1930701987654','2026-03-24 15:31:23.098325','1990-12-18','Sală – 4 zile pe săptămână, focus pe exerciții cu greutăți și puțin cardio la final','DA','5f89ace3-454d-486a-accd-986caa20d84a',8,'București','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-24 19:19:48.000000'),(11,_binary '','2940304123456','2026-03-24 15:39:16.363636','1999-09-09','Alergare – de 3 ori pe săptămână','DA','8bd48465-32ec-413b-9985-9a57a03a3d88',2,'București','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-03-24 19:19:48.000000'),(12,_binary '','2890918456789','2026-03-24 15:40:59.882398','1989-08-29','Drumeții – mai rar, dar ies în natură de câteva ori pe lună','DA','5ff54c13-8ec1-40b3-b147-bbf2ad48fb86',9,'Cluj-Napoca','69f7e2ae-39c0-4506-8558-d782493821a0','2026-03-24 19:19:48.000000'),(13,_binary '','2950923123456','2026-03-24 15:44:05.171495','2001-05-26','Fitness/aerobic – merg la clase de grup de 2-3 ori pe săptămână','DA','ac0ef5eb-34df-4a8c-8f4a-e879428aed2d',13,'Brașov','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-03-24 19:19:48.000000'),(14,_binary '','2870517123456','2026-03-24 15:51:44.704829','1961-08-18',NULL,'NU','31d03fc1-1800-4a52-9817-0c6b21d78e14',7,'București','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-24 19:19:48.000000'),(15,_binary '','1920223123456','2026-03-24 15:53:44.090292','1975-02-23','Tenis – ocazional, cam o dată la două săptămâni','DA','6da1727a-5388-4bfb-b845-d96485c61498',13,'Brașov','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-03-24 19:19:48.000000'),(16,_binary '','1911225123456','2026-03-24 15:55:54.852703','2007-05-01','Ciclism – ies cu bicicleta în weekend','DA','6c400be7-84b0-40f9-8854-b90e6c8358d9',10,'Cluj-Napoca','cb3ac7f5-4730-44cf-a053-d17f451f4b02','2026-03-24 19:19:48.000000'),(17,_binary '','1901111123456','2026-03-24 16:05:36.325908','1993-07-22','Mers pe jos – încerc să fac 8-10k pași pe zi','DA','487da990-f8ed-437f-b5d9-e9e2f5aef199',9,'Cluj-Napoca','69f7e2ae-39c0-4506-8558-d782493821a0','2026-03-24 19:19:48.000000'),(18,_binary '','2940402123456','2026-03-24 16:11:44.623212','2005-04-13','Înot – o dată sau de două ori pe săptămână','DA','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',2,'București','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-03-24 19:19:48.000000'),(19,_binary '','2960711123456','2026-03-24 16:17:37.415117','1959-12-25',NULL,'NU','235aa647-1c6c-479f-aa26-011539b51b73',1,'București','496b7af4-d378-418d-8d5f-5647ed31d11d','2026-03-24 19:19:51.000000'),(20,_binary '','2960225123456','2026-03-25 15:42:45.007650','1996-02-25','Alergare ușoară de 3 ori pe săptămână','DA','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70',7,'București','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-25 17:59:17.000000'),(21,_binary '','1850718123456','2026-03-25 15:45:06.951220','2003-08-16',NULL,'NU','00179244-a86e-4ade-8e22-3d70af6de57d',8,'București','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-25 16:18:14.786671'),(22,_binary '','2980305123456','2026-03-25 17:28:15.403746','1999-03-03',NULL,'NU','9f90d398-f030-4ce4-bfee-033758993378',7,NULL,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-03-25 17:38:18.281828');
/*!40000 ALTER TABLE `pacienti` ENABLE KEYS */;
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
