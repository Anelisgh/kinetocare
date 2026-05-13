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
/*!40101 SET character_set_client = @saved_cs_client */;

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
