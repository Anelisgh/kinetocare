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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conversatii`
--

LOCK TABLES `conversatii` WRITE;
/*!40000 ALTER TABLE `conversatii` DISABLE KEYS */;
INSERT INTO `conversatii` VALUES (5,'2026-06-07 18:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-06-07 20:35:00.000000','2026-06-07 20:35:00.000000'),(6,'2026-06-28 15:00:16.727138','00179244-a86e-4ade-8e22-3d70af6de57d','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-06-29 11:58:21.901893','2026-06-29 11:58:21.925200');
/*!40000 ALTER TABLE `conversatii` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mesaje`
--

LOCK TABLES `mesaje` WRITE;
/*!40000 ALTER TABLE `mesaje` DISABLE KEYS */;
INSERT INTO `mesaje` VALUES (15,'2026-06-07 20:10:00.000000','Bună seara, domnule terapeut! Aș dori să reprogramez ședința de mâine (luni, 8 iunie) pentru marți, 9 iunie, dacă este posibil.',5,_binary '','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','PACIENT','2026-06-07 18:00:00.000000'),(16,'2026-06-07 20:25:00.000000','Bună seara, Sabrina! Sigur că da. Te pot programa marți la ora 10:00. Este în regulă?',5,_binary '','51abc54c-0c2f-4f0a-aead-bdaa9627661e','TERAPEUT','2026-06-07 20:15:00.000000'),(17,'2026-06-07 20:30:00.000000','Da, este perfect! Vă mulțumesc frumos.',5,_binary '','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','PACIENT','2026-06-07 20:30:00.000000'),(18,'2026-06-07 20:36:00.000000','Cu drag! Am operat modificarea în sistem. Ne vedem marți.',5,_binary '','51abc54c-0c2f-4f0a-aead-bdaa9627661e','TERAPEUT','2026-06-07 20:35:00.000000'),(19,'2026-06-28 15:00:19.427591','test',6,_binary '','05e0f10e-2c4d-405e-abac-5ccedb83d2af','TERAPEUT','2026-06-28 15:00:16.948520'),(20,'2026-06-28 15:00:39.084645','buna ziua!',6,_binary '','00179244-a86e-4ade-8e22-3d70af6de57d','PACIENT','2026-06-28 15:00:38.956711'),(21,'2026-06-28 15:02:09.759644','test2',6,_binary '','05e0f10e-2c4d-405e-abac-5ccedb83d2af','TERAPEUT','2026-06-28 15:02:09.402395'),(22,'2026-06-28 15:02:21.213284','test3',6,_binary '','00179244-a86e-4ade-8e22-3d70af6de57d','PACIENT','2026-06-28 15:02:20.995292'),(23,'2026-06-28 15:08:44.105595','test4',6,_binary '','05e0f10e-2c4d-405e-abac-5ccedb83d2af','TERAPEUT','2026-06-28 15:08:19.834378'),(24,'2026-06-28 15:08:55.287355','test5',6,_binary '','05e0f10e-2c4d-405e-abac-5ccedb83d2af','TERAPEUT','2026-06-28 15:08:55.136261'),(25,'2026-06-28 15:09:58.698763','test6',6,_binary '','05e0f10e-2c4d-405e-abac-5ccedb83d2af','TERAPEUT','2026-06-28 15:09:58.521953'),(26,'2026-06-29 11:58:18.220621','test7',6,_binary '','05e0f10e-2c4d-405e-abac-5ccedb83d2af','TERAPEUT','2026-06-29 11:58:13.873521'),(27,'2026-06-29 11:58:22.264134','test8',6,_binary '','00179244-a86e-4ade-8e22-3d70af6de57d','PACIENT','2026-06-29 11:58:21.901892');
/*!40000 ALTER TABLE `mesaje` ENABLE KEYS */;
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
