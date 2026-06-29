-- MySQL dump 10.13  Distrib 8.0.35, for Win64 (x86_64)
--
-- Host: localhost    Database: notificari_service
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
-- Current Database: `notificari_service`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `notificari_service` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `notificari_service`;

--
-- Table structure for table `mesaje_procesate`
--

DROP TABLE IF EXISTS `mesaje_procesate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mesaje_procesate` (
  `message_id` varchar(36) NOT NULL,
  `processed_at` datetime(6) NOT NULL,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mesaje_procesate`
--

LOCK TABLES `mesaje_procesate` WRITE;
/*!40000 ALTER TABLE `mesaje_procesate` DISABLE KEYS */;
INSERT INTO `mesaje_procesate` VALUES ('0325e28c-5351-4976-85c1-c62be0d264e4','2026-06-28 18:02:54.000000'),('159cf2c8-451a-42a7-85ea-7160eb8e0426','2026-06-28 13:50:35.000000'),('174529eb-703e-47a7-a812-43db9c10670b','2026-06-29 14:34:54.000000'),('197a289f-5d0c-4d02-858a-11de7c92e2e0','2026-06-28 13:50:42.000000'),('22545f00-0682-4a3c-afeb-14cef302c126','2026-06-28 18:08:55.000000'),('259989b6-bcb6-4301-95f0-8ad4ec96d1bd','2026-06-28 13:50:42.000000'),('286c6307-fb17-4ff4-9d5a-dab199678320','2026-06-28 13:50:42.000000'),('4459db92-4e40-4cb0-94d5-a0643f3cf43a','2026-06-29 14:34:52.000000'),('457ed9f3-ea78-4fbd-bb94-335cd2cb01a4','2026-06-28 18:08:20.000000'),('48b11180-73e1-4671-bec8-ceeb5c38d21b','2026-06-28 13:50:42.000000'),('4b19dfa1-dbd9-43b5-9135-a226c28f3c7c','2026-06-29 14:34:54.000000'),('6203843c-50bd-4f29-9744-3201eea958be','2026-06-28 18:02:43.000000'),('64f288aa-3956-41e0-ad59-8c1e60f987d1','2026-06-29 14:58:21.000000'),('710e05a6-e846-43c2-9ed1-8177f7be63ec','2026-06-28 18:02:09.000000'),('72f0a0c8-4467-41f4-b228-59c07e9f5745','2026-06-29 14:34:50.000000'),('7d3a9a73-c6a8-4889-b200-8542ce7c3ece','2026-06-29 14:58:15.000000'),('83c0c803-5299-492b-80fc-e400f460d7a4','2026-06-29 14:34:54.000000'),('a5991322-85b4-4925-a107-5d182cdb4906','2026-06-28 18:02:21.000000'),('a794ef31-7288-4672-a915-3214510daab9','2026-06-29 14:34:54.000000'),('a7fec1b0-0780-4106-9e9e-111e3e267442','2026-06-28 18:09:58.000000'),('b7aff07e-c784-468e-a685-7d4d69a03103','2026-06-28 13:50:42.000000'),('c9fd7c65-b6e0-453a-819a-15561d7b7f7d','2026-06-29 14:34:53.000000'),('d0e8c237-4177-4907-bd16-842d0f6ad47e','2026-06-28 18:00:19.000000'),('de443b5e-7757-4c9b-b376-09a3d930737d','2026-06-29 14:34:53.000000'),('df6626ae-f2c3-41b9-8d92-3da2f97d99af','2026-06-28 18:00:38.000000'),('eababa10-7e90-417d-8eda-b86175f49def','2026-06-29 14:34:53.000000'),('f0a15c81-9e65-477a-9d31-dc1d47cfed1b','2026-06-28 13:50:42.000000');
/*!40000 ALTER TABLE `mesaje_procesate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notificari`
--

DROP TABLE IF EXISTS `notificari`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notificari` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `citita_la` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `entitate_legata_id` bigint DEFAULT NULL,
  `este_citita` bit(1) NOT NULL,
  `mesaj` text NOT NULL,
  `tip` enum('EVALUARE_INITIALA_NOUA','JURNAL_COMPLETAT','MESAJ_DE_LA_PACIENT','MESAJ_DE_LA_TERAPEUT','PROGRAMARE_ANULATA_DE_PACIENT','PROGRAMARE_ANULATA_DE_TERAPEUT','PROGRAMARE_NOUA','REEVALUARE_NECESARA','REEVALUARE_RECOMANDATA','REMINDER_24H','REMINDER_2H','REMINDER_JURNAL') NOT NULL,
  `tip_entitate_legata` varchar(50) DEFAULT NULL,
  `tip_user` enum('PACIENT','TERAPEUT') NOT NULL,
  `titlu` varchar(500) NOT NULL,
  `url_actiune` varchar(500) DEFAULT NULL,
  `user_keycloak_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_notif_user_kc` (`user_keycloak_id`),
  KEY `idx_notif_citita` (`este_citita`),
  KEY `idx_notif_creata` (`created_at`),
  KEY `idx_notif_user_query` (`user_keycloak_id`,`tip_user`,`este_citita`,`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notificari`
--

LOCK TABLES `notificari` WRITE;
/*!40000 ALTER TABLE `notificari` DISABLE KEYS */;
INSERT INTO `notificari` VALUES (51,'2026-05-28 11:30:00.000000','2026-05-28 10:00:00.000000',101,_binary '','O nouă programare a fost înregistrată pentru pacientul Sabrina Ene în data de 01.06.2026 la ora 10:00.','PROGRAMARE_NOUA','PROGRAMARE','TERAPEUT','Programare Noua','/terapeut/programari','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(52,'2026-06-07 20:15:00.000000','2026-06-07 18:00:00.000000',15,_binary '','Ai primit un mesaj nou de la pacientul Sabrina Ene: \"Bună seara, domnule terapeut! Aș dori...\"','MESAJ_DE_LA_PACIENT','MESAJ','TERAPEUT','Mesaj Nou de la Pacient','/terapeut/chat?pacient=a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(53,'2026-06-07 20:16:00.000000','2026-06-07 20:35:00.000000',104,_binary '','Programarea pacientului Sabrina Ene din data de 08.06.2026 a fost anulată de către pacient.','PROGRAMARE_ANULATA_DE_PACIENT','PROGRAMARE','TERAPEUT','Programare Anulată de Pacient','/terapeut/programari','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(54,'2026-06-02 08:30:00.000000','2026-06-01 18:00:00.000000',101,_binary '','Pacientul Sabrina Ene a completat jurnalul de simptome pentru ședința din 01.06.2026.','JURNAL_COMPLETAT','JURNAL','TERAPEUT','Jurnal Simptome Completat','/terapeut/pacienti/a2351fcd-9e0c-4ebc-b6fb-2530c9892556/jurnal','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(55,'2026-06-11 08:30:00.000000','2026-06-10 11:05:00.000000',106,_binary '','Cota de ședințe prescrise pentru Sabrina Ene a fost finalizată. Este necesară o reevaluare clinică.','REEVALUARE_NECESARA','PACIENT','TERAPEUT','Reevaluare Necesară','/terapeut/pacienti/a2351fcd-9e0c-4ebc-b6fb-2530c9892556/evaluari','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(56,'2026-06-07 20:30:00.000000','2026-06-07 20:15:00.000000',16,_binary '','Ai primit un mesaj nou de la terapeutul tău, Vlad Radu.','MESAJ_DE_LA_TERAPEUT','MESAJ','PACIENT','Mesaj Nou de la Terapeut','/pacient/chat','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),(57,'2026-06-12 11:30:00.000000','2026-06-12 10:35:00.000000',21,_binary '','Terapeutul tău a finalizat reevaluarea clinică. A fost recomandat un nou plan de tratament (4 ședințe de stabilizare).','REEVALUARE_RECOMANDATA','EVALUARE','PACIENT','Plan Nou Recomandat','/pacient/plan-tratament','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),(58,NULL,'2026-06-28 10:50:39.903282',204,_binary '\0','Ședința din 26 iunie a fost finalizată. Nu uita să completezi jurnalul!','REMINDER_JURNAL','PROGRAMARE','PACIENT','Completează jurnalul','/jurnal','072ee168-7759-4613-96e1-226f0d7277ea'),(59,NULL,'2026-06-28 10:50:42.745739',208,_binary '\0','Ședința din 26 iunie a fost finalizată. Nu uita să completezi jurnalul!','REMINDER_JURNAL','PROGRAMARE','PACIENT','Completează jurnalul','/jurnal','e61befe0-0110-45b7-9a31-8f433b113909'),(60,NULL,'2026-06-28 10:50:42.790859',208,_binary '\0','Ai finalizat toate ședințele recomandate. Te rugăm să te programezi pentru o re-evaluare.','REEVALUARE_RECOMANDATA','PROGRAMARE','PACIENT','Re-evaluare recomandată','/programari','e61befe0-0110-45b7-9a31-8f433b113909'),(61,NULL,'2026-06-28 10:50:42.872749',226,_binary '\0','Ședința din 26 iunie a fost finalizată. Nu uita să completezi jurnalul!','REMINDER_JURNAL','PROGRAMARE','PACIENT','Completează jurnalul','/jurnal','487da990-f8ed-437f-b5d9-e9e2f5aef199'),(62,NULL,'2026-06-28 10:50:42.904131',226,_binary '\0','Ai finalizat toate ședințele recomandate. Te rugăm să te programezi pentru o re-evaluare.','REEVALUARE_RECOMANDATA','PROGRAMARE','PACIENT','Re-evaluare recomandată','/programari','487da990-f8ed-437f-b5d9-e9e2f5aef199'),(63,NULL,'2026-06-28 10:50:42.947937',384,_binary '\0','Ședința din 26 iunie a fost finalizată. Nu uita să completezi jurnalul!','REMINDER_JURNAL','PROGRAMARE','PACIENT','Completează jurnalul','/jurnal','5ff54c13-8ec1-40b3-b147-bbf2ad48fb86'),(64,NULL,'2026-06-28 10:50:42.989709',414,_binary '\0','Ședința din 26 iunie a fost finalizată. Nu uita să completezi jurnalul!','REMINDER_JURNAL','PROGRAMARE','PACIENT','Completează jurnalul','/jurnal','9f90d398-f030-4ce4-bfee-033758993378'),(65,'2026-06-28 15:00:29.927557','2026-06-28 15:00:20.259661',6,_binary '','Ai primit un mesaj nou.','MESAJ_DE_LA_TERAPEUT','CONVERSATIE','PACIENT','Mesaj Nou','/chat/6','00179244-a86e-4ade-8e22-3d70af6de57d'),(66,'2026-06-28 15:00:44.460407','2026-06-28 15:00:39.010087',6,_binary '','Ai primit un mesaj nou.','MESAJ_DE_LA_PACIENT','CONVERSATIE','TERAPEUT','Mesaj Nou','/chat/6','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),(67,NULL,'2026-06-28 15:02:09.696787',6,_binary '\0','Ai primit un mesaj nou.','MESAJ_DE_LA_TERAPEUT','CONVERSATIE','PACIENT','Mesaj Nou','/chat/6','00179244-a86e-4ade-8e22-3d70af6de57d'),(68,NULL,'2026-06-28 15:02:21.077505',6,_binary '\0','Ai primit un mesaj nou.','MESAJ_DE_LA_PACIENT','CONVERSATIE','TERAPEUT','Mesaj Nou','/chat/6','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),(69,NULL,'2026-06-28 15:02:43.861922',631,_binary '\0','O programare din 01 iulie la ora 09:00 a fost anulată de pacient.','PROGRAMARE_ANULATA_DE_PACIENT','PROGRAMARE','TERAPEUT','Programare anulată de pacient','/calendar','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),(70,NULL,'2026-06-28 15:02:54.684727',632,_binary '\0','Ai o programare nouă pe 01 iulie la ora 10:00.','PROGRAMARE_NOUA','PROGRAMARE','TERAPEUT','Programare nouă','/calendar','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),(71,NULL,'2026-06-28 15:08:20.233524',6,_binary '\0','Ai primit un mesaj nou.','MESAJ_DE_LA_TERAPEUT','CONVERSATIE','PACIENT','Mesaj Nou','/chat/6','00179244-a86e-4ade-8e22-3d70af6de57d'),(72,NULL,'2026-06-28 15:08:55.191298',6,_binary '\0','Ai primit un mesaj nou.','MESAJ_DE_LA_TERAPEUT','CONVERSATIE','PACIENT','Mesaj Nou','/chat/6','00179244-a86e-4ade-8e22-3d70af6de57d'),(73,NULL,'2026-06-28 15:09:58.590292',6,_binary '\0','Ai primit un mesaj nou.','MESAJ_DE_LA_TERAPEUT','CONVERSATIE','PACIENT','Mesaj Nou','/chat/6','00179244-a86e-4ade-8e22-3d70af6de57d'),(74,NULL,'2026-06-29 11:34:51.523648',205,_binary '\0','Ședința din 29 iunie a fost finalizată. Nu uita să completezi jurnalul!','REMINDER_JURNAL','PROGRAMARE','PACIENT','Completează jurnalul','/jurnal','072ee168-7759-4613-96e1-226f0d7277ea'),(75,NULL,'2026-06-29 11:34:52.836414',205,_binary '\0','Ai finalizat toate ședințele recomandate. Te rugăm să te programezi pentru o re-evaluare.','REEVALUARE_RECOMANDATA','PROGRAMARE','PACIENT','Re-evaluare recomandată','/programari','072ee168-7759-4613-96e1-226f0d7277ea'),(76,NULL,'2026-06-29 11:34:53.187953',385,_binary '\0','Ședința din 29 iunie a fost finalizată. Nu uita să completezi jurnalul!','REMINDER_JURNAL','PROGRAMARE','PACIENT','Completează jurnalul','/jurnal','5ff54c13-8ec1-40b3-b147-bbf2ad48fb86'),(77,NULL,'2026-06-29 11:34:53.534933',385,_binary '\0','Ai finalizat toate ședințele recomandate. Te rugăm să te programezi pentru o re-evaluare.','REEVALUARE_RECOMANDATA','PROGRAMARE','PACIENT','Re-evaluare recomandată','/programari','5ff54c13-8ec1-40b3-b147-bbf2ad48fb86'),(78,NULL,'2026-06-29 11:34:53.905546',396,_binary '\0','Ședința din 29 iunie a fost finalizată. Nu uita să completezi jurnalul!','REMINDER_JURNAL','PROGRAMARE','PACIENT','Completează jurnalul','/jurnal','ac0ef5eb-34df-4a8c-8f4a-e879428aed2d'),(79,NULL,'2026-06-29 11:34:54.264147',403,_binary '\0','Ședința din 29 iunie a fost finalizată. Nu uita să completezi jurnalul!','REMINDER_JURNAL','PROGRAMARE','PACIENT','Completează jurnalul','/jurnal','235aa647-1c6c-479f-aa26-011539b51b73'),(80,NULL,'2026-06-29 11:34:54.687476',403,_binary '\0','Ai finalizat toate ședințele recomandate. Te rugăm să te programezi pentru o re-evaluare.','REEVALUARE_RECOMANDATA','PROGRAMARE','PACIENT','Re-evaluare recomandată','/programari','235aa647-1c6c-479f-aa26-011539b51b73'),(81,NULL,'2026-06-29 11:34:54.737092',415,_binary '\0','Ședința din 29 iunie a fost finalizată. Nu uita să completezi jurnalul!','REMINDER_JURNAL','PROGRAMARE','PACIENT','Completează jurnalul','/jurnal','9f90d398-f030-4ce4-bfee-033758993378'),(82,NULL,'2026-06-29 11:34:54.789759',415,_binary '\0','Ai finalizat toate ședințele recomandate. Te rugăm să te programezi pentru o re-evaluare.','REEVALUARE_RECOMANDATA','PROGRAMARE','PACIENT','Re-evaluare recomandată','/programari','9f90d398-f030-4ce4-bfee-033758993378'),(83,NULL,'2026-06-29 11:58:15.461473',6,_binary '\0','Ai primit un mesaj nou.','MESAJ_DE_LA_TERAPEUT','CONVERSATIE','PACIENT','Mesaj Nou','/chat/6','00179244-a86e-4ade-8e22-3d70af6de57d'),(84,NULL,'2026-06-29 11:58:21.968367',6,_binary '\0','Ai primit un mesaj nou.','MESAJ_DE_LA_PACIENT','CONVERSATIE','TERAPEUT','Mesaj Nou','/chat/6','05e0f10e-2c4d-405e-abac-5ccedb83d2af');
/*!40000 ALTER TABLE `notificari` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-29 16:43:54
