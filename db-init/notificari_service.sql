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
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `notificari`
--

LOCK TABLES `notificari` WRITE;
/*!40000 ALTER TABLE `notificari` DISABLE KEYS */;
INSERT INTO `notificari` (`id`, `citita_la`, `created_at`, `entitate_legata_id`, `este_citita`, `mesaj`, `tip`, `tip_entitate_legata`, `tip_user`, `titlu`, `url_actiune`, `user_keycloak_id`) VALUES (51,'2026-05-28 11:30:00.000000','2026-05-28 10:00:00.000000',101,b'1','O nouă programare a fost înregistrată pentru pacientul Sabrina Ene în data de 01.06.2026 la ora 10:00.','PROGRAMARE_NOUA','PROGRAMARE','TERAPEUT','Programare Noua','/terapeut/programari','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(52,'2026-06-07 20:15:00.000000','2026-06-07 18:00:00.000000',15,b'1','Ai primit un mesaj nou de la pacientul Sabrina Ene: "Bună seara, domnule terapeut! Aș dori..."','MESAJ_DE_LA_PACIENT','MESAJ','TERAPEUT','Mesaj Nou de la Pacient','/terapeut/chat?pacient=a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(53,'2026-06-07 20:16:00.000000','2026-06-07 20:35:00.000000',104,b'1','Programarea pacientului Sabrina Ene din data de 08.06.2026 a fost anulată de către pacient.','PROGRAMARE_ANULATA_DE_PACIENT','PROGRAMARE','TERAPEUT','Programare Anulată de Pacient','/terapeut/programari','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(54,'2026-06-02 08:30:00.000000','2026-06-01 18:00:00.000000',101,b'1','Pacientul Sabrina Ene a completat jurnalul de simptome pentru ședința din 01.06.2026.','JURNAL_COMPLETAT','JURNAL','TERAPEUT','Jurnal Simptome Completat','/terapeut/pacienti/a2351fcd-9e0c-4ebc-b6fb-2530c9892556/jurnal','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(55,'2026-06-11 08:30:00.000000','2026-06-10 11:05:00.000000',106,b'1','Cota de ședințe prescrise pentru Sabrina Ene a fost finalizată. Este necesară o reevaluare clinică.','REEVALUARE_NECESARA','PACIENT','TERAPEUT','Reevaluare Necesară','/terapeut/pacienti/a2351fcd-9e0c-4ebc-b6fb-2530c9892556/evaluari','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(56,'2026-06-07 20:30:00.000000','2026-06-07 20:15:00.000000',16,b'1','Ai primit un mesaj nou de la terapeutul tău, Vlad Radu.','MESAJ_DE_LA_TERAPEUT','MESAJ','PACIENT','Mesaj Nou de la Terapeut','/pacient/chat','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),(57,'2026-06-12 11:30:00.000000','2026-06-12 10:35:00.000000',21,b'1','Terapeutul tău a finalizat reevaluarea clinică. A fost recomandat un nou plan de tratament (4 ședințe de stabilizare).','REEVALUARE_RECOMANDATA','EVALUARE','PACIENT','Plan Nou Recomandat','/pacient/plan-tratament','a2351fcd-9e0c-4ebc-b6fb-2530c9892556');
/*!40000 ALTER TABLE `notificari` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `mesaje_procesate`;
CREATE TABLE `mesaje_procesate` (
  `message_id` varchar(36) NOT NULL,
  `processed_at` datetime(6) NOT NULL,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `notificari`
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-13 13:39:54
