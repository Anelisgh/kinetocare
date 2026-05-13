-- MySQL dump 10.13  Distrib 8.0.35, for Win64 (x86_64)
--
-- Host: localhost    Database: chat_service
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
-- Current Database: `chat_service`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `chat_service` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `chat_service`;

--
-- Table structure for table `conversatii`
--

DROP TABLE IF EXISTS `conversatii`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conversatii` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `pacient_keycloak_id` varchar(36) NOT NULL,
  `terapeut_keycloak_id` varchar(36) NOT NULL,
  `ultimul_mesaj_la` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_conv_pacient` (`pacient_keycloak_id`),
  KEY `idx_conv_terapeut` (`terapeut_keycloak_id`),
  KEY `idx_conversatii_ultimul_mesaj_la` (`ultimul_mesaj_la`),
  KEY `idx_conv_participants` (`pacient_keycloak_id`,`terapeut_keycloak_id`),
  KEY `idx_conv_ultim_mesaj` (`ultimul_mesaj_la`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `mesaje`
--

DROP TABLE IF EXISTS `mesaje`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mesaje` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `citit_la` datetime(6) DEFAULT NULL,
  `continut` text NOT NULL,
  `conversatie_id` bigint NOT NULL,
  `este_citit` bit(1) NOT NULL,
  `expeditor_keycloak_id` varchar(36) NOT NULL,
  `tip_expeditor` enum('PACIENT','TERAPEUT') NOT NULL,
  `trimis_la` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_mesaj_conversatie` (`conversatie_id`),
  KEY `idx_mesaj_expeditor` (`expeditor_keycloak_id`),
  KEY `idx_mesaj_trimis_la` (`trimis_la`),
  KEY `idx_mesaj_conv_list` (`conversatie_id`,`trimis_la`),
  KEY `idx_mesaje_conversatie_id_trimis_la` (`conversatie_id`,`trimis_la`),
  KEY `idx_mesaje_expeditor_keycloak_id` (`expeditor_keycloak_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mesaje`
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
