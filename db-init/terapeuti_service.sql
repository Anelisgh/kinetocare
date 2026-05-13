-- MySQL dump 10.13  Distrib 8.0.35, for Win64 (x86_64)
--
-- Host: localhost    Database: terapeuti_service
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
-- Current Database: `terapeuti_service`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `terapeuti_service` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `terapeuti_service`;

--
-- Table structure for table `concediu_terapeut`
--

DROP TABLE IF EXISTS `concediu_terapeut`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `concediu_terapeut` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `data_inceput` date NOT NULL,
  `data_sfarsit` date NOT NULL,
  `terapeut_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_concediu_terapeut` (`terapeut_id`),
  KEY `idx_concediu_date` (`data_inceput`,`data_sfarsit`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `concediu_terapeut`
--

LOCK TABLES `concediu_terapeut` WRITE;
/*!40000 ALTER TABLE `concediu_terapeut` DISABLE KEYS */;
INSERT INTO `concediu_terapeut` VALUES (1,'2026-02-28 12:27:45.433325','2026-03-20','2026-03-21',1);
/*!40000 ALTER TABLE `concediu_terapeut` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `disponibilitate_terapeut`
--

DROP TABLE IF EXISTS `disponibilitate_terapeut`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `disponibilitate_terapeut` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `locatie_id` bigint NOT NULL,
  `ora_inceput` time(6) NOT NULL,
  `ora_sfarsit` time(6) NOT NULL,
  `terapeut_id` bigint NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `zi_saptamana` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_disp_terapeut` (`terapeut_id`),
  KEY `idx_disp_locatie` (`locatie_id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `disponibilitate_terapeut`
--

LOCK TABLES `disponibilitate_terapeut` WRITE;
/*!40000 ALTER TABLE `disponibilitate_terapeut` DISABLE KEYS */;
INSERT INTO `disponibilitate_terapeut` VALUES (1,_binary '\0','2026-02-28 12:13:18.971525',1,'09:00:00.000000','17:00:00.000000',1,'2026-02-28 17:57:35.612644',1),(2,_binary '\0','2026-02-28 12:23:20.603823',1,'09:00:00.000000','17:00:00.000000',1,'2026-02-28 17:57:39.117033',2),(3,_binary '\0','2026-02-28 12:23:20.603823',1,'09:00:00.000000','17:00:00.000000',1,'2026-02-28 12:23:27.144569',2),(4,_binary '\0','2026-02-28 12:27:30.124464',1,'09:00:00.000000','17:00:00.000000',1,'2026-02-28 17:57:37.169323',3),(5,_binary '','2026-02-28 18:11:50.592879',1,'09:00:00.000000','17:00:00.000000',1,'2026-02-28 18:11:50.592879',1),(6,_binary '','2026-02-28 18:11:53.278640',1,'09:00:00.000000','17:00:00.000000',1,'2026-02-28 18:11:53.278640',2),(7,_binary '','2026-02-28 18:11:56.006745',1,'09:00:00.000000','17:00:00.000000',1,'2026-02-28 18:11:56.006745',3),(8,_binary '\0','2026-02-28 18:11:59.988031',2,'09:00:00.000000','17:00:00.000000',1,'2026-03-02 15:12:29.194751',4),(9,_binary '\0','2026-02-28 18:12:05.022616',2,'09:00:00.000000','17:00:00.000000',1,'2026-02-28 18:56:58.135181',5),(10,_binary '','2026-02-28 18:57:02.113180',1,'09:00:00.000000','17:00:00.000000',1,'2026-02-28 18:57:02.113180',5),(11,_binary '','2026-03-02 15:12:34.636895',1,'09:00:00.000000','17:00:00.000000',1,'2026-03-02 15:12:34.636895',4),(12,_binary '\0','2026-03-02 15:12:37.364998',1,'09:00:00.000000','17:00:00.000000',1,'2026-03-16 18:20:37.599120',6),(13,_binary '','2026-03-24 19:15:15.000000',9,'08:00:00.000000','16:00:00.000000',2,'2026-03-24 19:15:15.000000',1),(14,_binary '','2026-03-24 19:15:15.000000',9,'08:00:00.000000','16:00:00.000000',2,'2026-03-24 19:15:15.000000',2),(15,_binary '','2026-03-24 19:15:15.000000',9,'08:00:00.000000','16:00:00.000000',2,'2026-03-24 19:15:15.000000',3),(16,_binary '','2026-03-24 19:15:15.000000',7,'09:00:00.000000','14:00:00.000000',3,'2026-03-24 19:15:15.000000',1),(17,_binary '','2026-03-24 19:15:15.000000',7,'09:00:00.000000','14:00:00.000000',3,'2026-03-24 19:15:15.000000',2),(18,_binary '','2026-03-24 19:15:15.000000',8,'14:00:00.000000','20:00:00.000000',3,'2026-03-24 19:15:15.000000',4),(19,_binary '','2026-03-24 19:15:15.000000',8,'14:00:00.000000','20:00:00.000000',3,'2026-03-24 19:15:15.000000',5),(20,_binary '','2026-03-24 19:15:15.000000',2,'10:00:00.000000','18:00:00.000000',4,'2026-03-24 19:15:15.000000',1),(21,_binary '','2026-03-24 19:15:15.000000',2,'10:00:00.000000','18:00:00.000000',4,'2026-03-24 19:15:15.000000',3),(22,_binary '','2026-03-24 19:15:15.000000',1,'09:00:00.000000','15:00:00.000000',5,'2026-03-24 19:15:15.000000',2),(23,_binary '','2026-03-24 19:15:15.000000',1,'09:00:00.000000','15:00:00.000000',5,'2026-03-24 19:15:15.000000',4),(24,_binary '','2026-03-24 19:15:15.000000',10,'12:00:00.000000','20:00:00.000000',6,'2026-03-24 19:15:15.000000',1),(25,_binary '','2026-03-24 19:15:15.000000',10,'12:00:00.000000','20:00:00.000000',6,'2026-03-24 19:15:15.000000',3),(26,_binary '','2026-03-24 19:15:16.000000',13,'08:00:00.000000','16:00:00.000000',7,'2026-03-24 19:15:16.000000',1),(27,_binary '','2026-03-24 19:15:16.000000',13,'08:00:00.000000','16:00:00.000000',7,'2026-03-24 19:15:16.000000',2),(28,_binary '','2026-03-24 19:42:29.904978',9,'08:00:00.000000','16:00:00.000000',2,'2026-03-24 19:42:29.904978',4),(29,_binary '','2026-03-24 19:42:29.904978',9,'08:00:00.000000','16:00:00.000000',2,'2026-03-24 19:42:29.904978',5),(30,_binary '\0','2026-03-24 19:42:29.927972',7,'09:00:00.000000','14:00:00.000000',3,'2026-03-24 18:22:41.103206',3),(31,_binary '','2026-03-24 19:42:29.933641',2,'10:00:00.000000','18:00:00.000000',4,'2026-03-24 19:42:29.933641',2),(32,_binary '','2026-03-24 19:42:29.933641',2,'10:00:00.000000','18:00:00.000000',4,'2026-03-24 19:42:29.933641',4),(33,_binary '','2026-03-24 19:42:29.933641',2,'10:00:00.000000','18:00:00.000000',4,'2026-03-24 19:42:29.933641',5),(34,_binary '','2026-03-24 19:42:29.938759',1,'09:00:00.000000','15:00:00.000000',5,'2026-03-24 19:42:29.938759',1),(35,_binary '','2026-03-24 19:42:29.938759',1,'09:00:00.000000','15:00:00.000000',5,'2026-03-24 19:42:29.938759',3),(36,_binary '','2026-03-24 19:42:29.938759',1,'09:00:00.000000','15:00:00.000000',5,'2026-03-24 19:42:29.938759',5),(37,_binary '','2026-03-24 19:42:29.943826',10,'12:00:00.000000','20:00:00.000000',6,'2026-03-24 19:42:29.943826',2),(38,_binary '','2026-03-24 19:42:29.943826',10,'12:00:00.000000','20:00:00.000000',6,'2026-03-24 19:42:29.943826',4),(39,_binary '','2026-03-24 19:42:29.943826',10,'12:00:00.000000','20:00:00.000000',6,'2026-03-24 19:42:29.943826',5),(40,_binary '','2026-03-24 19:42:29.951535',13,'08:00:00.000000','16:00:00.000000',7,'2026-03-24 19:42:29.951535',3),(41,_binary '','2026-03-24 19:42:29.951535',13,'08:00:00.000000','16:00:00.000000',7,'2026-03-24 19:42:29.951535',4),(42,_binary '','2026-03-24 19:42:29.951535',13,'08:00:00.000000','16:00:00.000000',7,'2026-03-24 19:42:29.951535',5),(43,_binary '\0','2026-03-24 18:22:48.976203',8,'09:00:00.000000','17:00:00.000000',3,'2026-03-24 18:22:55.368214',3),(44,_binary '\0','2026-03-24 18:23:10.730173',8,'09:00:00.000000','14:00:00.000000',3,'2026-03-24 18:23:14.588893',1),(45,_binary '','2026-03-24 18:23:21.669448',8,'09:00:00.000000','14:00:00.000000',3,'2026-03-24 18:23:21.669448',3),(46,_binary '','2026-03-25 17:22:43.053069',7,'09:00:00.000000','17:00:00.000000',1,'2026-03-25 17:22:43.053069',6);
/*!40000 ALTER TABLE `disponibilitate_terapeut` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `locatii`
--

DROP TABLE IF EXISTS `locatii`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `locatii` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `adresa` varchar(300) NOT NULL,
  `cod_postal` varchar(10) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `judet` varchar(100) NOT NULL,
  `nume` varchar(200) NOT NULL,
  `oras` varchar(100) NOT NULL,
  `telefon` varchar(20) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_locatii_oras` (`oras`),
  KEY `idx_locatii_active` (`active`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `locatii`
--

LOCK TABLES `locatii` WRITE;
/*!40000 ALTER TABLE `locatii` DISABLE KEYS */;
INSERT INTO `locatii` VALUES (1,_binary '','Calea Moșilor 158','020883','2026-02-28 11:50:41.595483','București',' Kineto Bebe Mosilor','București','0727988679','2026-02-28 11:50:41.597500'),(2,_binary '','Str. Nicolae G. Caramfil','077190','2026-02-28 11:51:42.497185','București','Kineto Bebe Nord','București','0727988679','2026-02-28 11:51:42.499679'),(7,_binary '','Str. Liviu Rebreanu 25','031754','2026-03-24 18:52:12.104065','București','KinetoPlus Titan','București','0723551122','2026-03-24 18:52:12.104065'),(8,_binary '','Bd. Drumul Taberei 141','061402','2026-03-24 18:52:12.104065','București','Postural Kinetic','București','0732123456','2026-03-24 18:52:12.104065'),(9,_binary '','Str. Louis Pasteur 72','400349','2026-03-24 18:52:15.320825','Cluj','KinetoHealth','Cluj-Napoca','0746123000','2026-03-24 18:52:15.320825'),(10,_binary '','Str. Observatorului 111','400432','2026-03-24 18:52:15.320825','Cluj','PhysioMotion','Cluj-Napoca','0749982210','2026-03-24 18:52:15.320825'),(11,_binary '','Calea Dorobanților 18','400117','2026-03-24 18:52:15.320825','Cluj','Rehab Center','Cluj-Napoca','0756110099','2026-03-24 18:52:15.320825'),(12,_binary '','Str. Sărărie 85','700454','2026-03-24 18:52:17.537895','Iași','MovKinetic','Iași','0770441188','2026-03-24 18:52:17.537895'),(13,_binary '','Str. Michael Weiss 17','500031','2026-03-24 18:52:19.367098','Brașov','Kinetomed','Brașov','0739557766','2026-03-24 18:52:19.367098'),(14,_binary '','Bd. Gării 3A','500218','2026-03-24 18:52:19.367098','Brașov','RehabMotion','Brașov','0741993302','2026-03-24 18:52:19.367098');
/*!40000 ALTER TABLE `locatii` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `terapeuti`
--

DROP TABLE IF EXISTS `terapeuti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `terapeuti` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `keycloak_id` varchar(36) NOT NULL,
  `poza_profil` mediumtext,
  `specializare` enum('ADULTI','PEDIATRIE') DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_terapeuti_keycloak_id` (`keycloak_id`),
  KEY `idx_terapeuti_specializare` (`specializare`),
  KEY `idx_terapeuti_specializare_active` (`specializare`,`active`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `terapeuti`
--

LOCK TABLES `terapeuti` WRITE;
/*!40000 ALTER TABLE `terapeuti` DISABLE KEYS */;
INSERT INTO `terapeuti` VALUES (1,_binary '','2026-02-28 11:54:21.006103','496b7af4-d378-418d-8d5f-5647ed31d11d',NULL,'ADULTI','2026-02-28 19:22:36.116652'),(2,_binary '','2026-03-16 18:30:05.247270','69f7e2ae-39c0-4506-8558-d782493821a0',NULL,'PEDIATRIE','2026-03-16 18:30:05.250767'),(3,_binary '','2026-03-24 15:24:58.026892','05e0f10e-2c4d-405e-abac-5ccedb83d2af',NULL,'ADULTI','2026-03-25 17:21:08.659647'),(4,_binary '','2026-03-24 15:35:03.954265','51abc54c-0c2f-4f0a-aead-bdaa9627661e',NULL,'ADULTI','2026-03-24 15:35:03.954265'),(5,_binary '','2026-03-24 15:35:40.703110','f90f7761-c2ee-41f5-92fb-96c47b6567f5',NULL,'PEDIATRIE','2026-03-24 15:35:40.703110'),(6,_binary '','2026-03-24 15:37:01.732443','cb3ac7f5-4730-44cf-a053-d17f451f4b02',NULL,'ADULTI','2026-03-24 15:37:01.732443'),(7,_binary '','2026-03-24 15:38:14.737437','d407ab1c-7554-456d-9726-3f5571fa8fd0',NULL,'ADULTI','2026-03-24 15:38:14.737437');
/*!40000 ALTER TABLE `terapeuti` ENABLE KEYS */;
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
