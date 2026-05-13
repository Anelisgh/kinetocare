-- MySQL dump 10.13  Distrib 8.0.35, for Win64 (x86_64)
--
-- Host: localhost    Database: servicii_service
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
-- Current Database: `servicii_service`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `servicii_service` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `servicii_service`;

--
-- Table structure for table `servicii`
--

DROP TABLE IF EXISTS `servicii`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicii` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `durata_minute` int NOT NULL,
  `nume` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pret` decimal(10,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `tip_serviciu_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_servicii_tip` (`tip_serviciu_id`),
  KEY `idx_serviciu_nume` (`nume`),
  CONSTRAINT `FKpom304b3tnq7xffr7rne34a0x` FOREIGN KEY (`tip_serviciu_id`) REFERENCES `tip_serviciu` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servicii`
--

LOCK TABLES `servicii` WRITE;
/*!40000 ALTER TABLE `servicii` DISABLE KEYS */;
INSERT INTO `servicii` VALUES (1,_binary '','2026-02-28 11:49:25.270994',30,'Evaluare Initiala',150.00,'2026-03-23 18:49:05.000000',1),(2,_binary '','2026-02-28 11:49:37.315817',30,'Reevaluare',100.00,'2026-03-23 18:47:08.000000',1),(6,_binary '','2026-03-23 18:51:51.000000',50,'Ședință individuală',100.00,'2026-03-23 18:51:51.000000',4),(7,_binary '','2026-03-23 18:51:51.000000',60,'Recuperare post-operatorie',150.00,'2026-03-23 18:51:51.000000',4),(8,_binary '','2026-03-23 18:51:51.000000',50,'Gimnastică profilactică',110.00,'2026-03-23 18:51:51.000000',4),(9,_binary '','2026-03-23 18:52:35.000000',20,'Electroterapie (TENS)',50.00,'2026-03-23 18:52:35.000000',5),(10,_binary '','2026-03-23 18:52:35.000000',15,'Terapie cu Ultrasunete',60.00,'2026-03-23 18:52:35.000000',5),(11,_binary '','2026-03-23 18:52:35.000000',10,'Laserterapie',50.00,'2026-03-23 18:52:35.000000',5),(12,_binary '','2026-03-23 18:54:12.000000',15,'Aplicare Kinesio Taping',40.00,'2026-03-23 18:54:12.000000',6),(13,_binary '','2026-03-23 18:54:12.000000',30,'Terapie TECAR',150.00,'2026-03-23 18:54:12.000000',6),(14,_binary '','2026-03-23 18:54:16.000000',30,'Masaj terapeutic regional',90.00,'2026-03-23 18:54:16.000000',7),(15,_binary '','2026-03-23 18:54:16.000000',50,'Drenaj limfatic manual',130.00,'2026-03-23 18:54:16.000000',7);
/*!40000 ALTER TABLE `servicii` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tip_serviciu`
--

DROP TABLE IF EXISTS `tip_serviciu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tip_serviciu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `descriere` text COLLATE utf8mb4_unicode_ci,
  `nume` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKb3q5assjdgtplxgwp1pfr5qqx` (`nume`),
  KEY `idx_tip_serviciu_nume` (`nume`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tip_serviciu`
--

LOCK TABLES `tip_serviciu` WRITE;
/*!40000 ALTER TABLE `tip_serviciu` DISABLE KEYS */;
INSERT INTO `tip_serviciu` VALUES (1,_binary '','2026-02-28 11:47:32.489525','Punctul de plecare pentru orice program de recuperare.','Evaluare','2026-03-23 18:44:49.000000'),(4,_binary '','2026-02-28 18:58:58.702824','Recuperare medicală bazată pe programe de exerciții fizice individualizate, menite să refacă funcțiile motrice, să crească mobilitatea articulară și să îmbunătățească forța musculară.','Kinetoterapie','2026-03-23 18:44:54.000000'),(5,_binary '','2026-03-23 18:45:12.000000','Tratamente ce utilizează agenți fizici (curenți electrici, ultrasunete, laser) pentru combaterea durerii, eliminarea contracturilor și stimularea circulației.','Fizioterapie','2026-03-23 18:45:12.000000'),(6,_binary '','2026-03-23 18:45:16.000000','Tehnologii avansate utilizate pentru accelerarea procesului de vindecare și reducerea inflamației.','Terapii Speciale','2026-03-23 18:45:16.000000'),(7,_binary '','2026-03-23 18:53:39.000000','Tehnici de manipulare a țesuturilor moi dedicate relaxării musculare, deblocării punctelor tensionate și îmbunătățirii stării generale de bine.','Masaj','2026-03-23 18:53:39.000000');
/*!40000 ALTER TABLE `tip_serviciu` ENABLE KEYS */;
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
