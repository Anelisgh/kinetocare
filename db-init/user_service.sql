-- MySQL dump 10.13  Distrib 8.0.35, for Win64 (x86_64)
--
-- Host: localhost    Database: user_service
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
-- Current Database: `user_service`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `user_service` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `user_service`;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(100) NOT NULL,
  `gen` enum('FEMININ','MASCULIN') NOT NULL,
  `keycloak_id` varchar(36) NOT NULL,
  `nume` varchar(100) NOT NULL,
  `prenume` varchar(100) NOT NULL,
  `role` enum('ADMIN','PACIENT','TERAPEUT') NOT NULL,
  `telefon` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`),
  UNIQUE KEY `uk_users_keycloak_id` (`keycloak_id`),
  KEY `idx_users_role` (`role`),
  KEY `idx_users_keycloak_id` (`keycloak_id`),
  KEY `idx_users_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,_binary '','2026-02-28 11:38:39.585127','admin@kinetocare.com','MASCULIN','2366f5cd-9906-4cd6-b1a0-649a47046c38','Admin','Kinetocare','ADMIN','0700000000'),(2,_binary '','2026-02-28 11:54:18.959948','clara@yahoo.com','FEMININ','496b7af4-d378-418d-8d5f-5647ed31d11d','Clara','Maria','TERAPEUT','0745782111'),(3,_binary '','2026-02-28 12:41:05.605653','freya@yahoo.com','FEMININ','2733282b-1ee8-4afb-827a-aa0ff0b98f61','Freya','Gh','PACIENT','0745782112'),(5,_binary '','2026-03-16 18:17:14.997232','alina@yahoo.com','FEMININ','072ee168-7759-4613-96e1-226f0d7277ea','Popescu','Alina','PACIENT','0745782145'),(10,_binary '','2026-03-24 15:11:31.693524','andrei@yahoo.com','MASCULIN','e61befe0-0110-45b7-9a31-8f433b113909','Popescu','Andrei','PACIENT','0723456981'),(11,_binary '','2026-03-24 15:16:26.200830','maria@yahoo.com','FEMININ','de85c053-a583-4219-8fe9-5afd66c0e812','Ionescu','Maria','PACIENT','0741892365'),(12,_binary '','2026-03-24 15:24:56.303923','alexandru@yahoo.com','MASCULIN','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Dumitrescu','Alexandru','TERAPEUT','0756234119'),(13,_binary '','2026-03-24 15:26:02.321669','elena@yahoo.com','FEMININ','77d841ba-ba92-4d98-a6bd-873c58fbc440','Gheorghe','Elena','PACIENT','0732667540'),(14,_binary '','2026-03-24 15:31:23.045745','mihai@yahoo.com','MASCULIN','5f89ace3-454d-486a-accd-986caa20d84a','Stan','Mihai','PACIENT','0761903228'),(15,_binary '','2026-03-24 15:35:03.813774','vlad@yahoo.com','MASCULIN','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Radu','Vlad','TERAPEUT','0784332901'),(16,_binary '','2026-03-24 15:35:40.675620','simona@yahoo.com','FEMININ','f90f7761-c2ee-41f5-92fb-96c47b6567f5','Ilie','Simona','TERAPEUT','0752990137'),(18,_binary '','2026-03-24 15:38:14.701389','sara@yahoo.com','FEMININ','d407ab1c-7554-456d-9726-3f5571fa8fd0','Munteanu','Sara','TERAPEUT','0748305771'),(19,_binary '','2026-03-24 15:39:16.121714','oana@yahoo.com','FEMININ','8bd48465-32ec-413b-9985-9a57a03a3d88','Pavel','Oana','PACIENT','0721663590'),(20,_binary '','2026-03-24 15:40:59.857803','daniel@yahoo.com','MASCULIN','5ff54c13-8ec1-40b3-b147-bbf2ad48fb86','Dobre','Daniel','PACIENT','0745778263'),(21,_binary '','2026-03-24 15:44:05.009013','ana@yahoo.com','FEMININ','ac0ef5eb-34df-4a8c-8f4a-e879428aed2d','Dumitrescu','Ana','PACIENT','0738456712'),(22,_binary '','2026-03-24 15:51:44.668664','mihaela@yahoo.com','FEMININ','31d03fc1-1800-4a52-9817-0c6b21d78e14','Neagu','Mihaela','PACIENT','0735887416'),(23,_binary '','2026-03-24 15:53:44.061682','robert@yahoo.com','MASCULIN','6da1727a-5388-4bfb-b845-d96485c61498','Tudor','Robert','PACIENT','0769221804'),(24,_binary '','2026-03-24 15:55:54.820331','cristian@yahoo.com','MASCULIN','6c400be7-84b0-40f9-8854-b90e6c8358d9','Marin','Cristian','PACIENT','0756112233'),(25,_binary '','2026-03-24 16:05:35.421751','sorin@yahoo.com','MASCULIN','487da990-f8ed-437f-b5d9-e9e2f5aef199','Sorin','Tudor','PACIENT','0769988776'),(26,_binary '','2026-03-24 16:11:44.544180','sabrina@yahoo.com','FEMININ','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','Ene','Sabrina','PACIENT','0732123456'),(27,_binary '','2026-03-24 16:17:37.311740','daniela@yahoo.com','FEMININ','235aa647-1c6c-479f-aa26-011539b51b73','Stan','Daniela','PACIENT','0729987612'),(28,_binary '','2026-03-25 15:42:43.490541','irina@yahoo.com','FEMININ','b6f3d53a-f2d3-4e2b-b531-d1463a88ef70','Ionescu','Irina','PACIENT','0745123499'),(29,_binary '','2026-03-25 15:45:06.529022','matei@yahoo.com','MASCULIN','00179244-a86e-4ade-8e22-3d70af6de57d','Mihnea','Matei','PACIENT','0768345120'),(30,_binary '','2026-03-25 17:28:13.374773','ana_maria@yahoo.com','FEMININ','9f90d398-f030-4ce4-bfee-033758993378','Popescu','Ana-Maria','PACIENT','0778321123');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
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
