import os

# Define paths to the original db-init files
db_init_dir = r"c:\Users\Aneliss\Desktop\kinetocare\db-init"
programari_file = os.path.join(db_init_dir, "programari_service.sql")
pacienti_file = os.path.join(db_init_dir, "pacienti_service.sql")
chat_file = os.path.join(db_init_dir, "chat_service.sql")
notificari_file = os.path.join(db_init_dir, "notificari_service.sql")

print("Starting injection of mock data into db-init SQL files...")

# ==============================================================================
# 1. Update chat_service.sql
# ==============================================================================
print("Updating chat_service.sql...")
with open(chat_file, "r", encoding="utf-8") as f:
    chat_content = f.read()

target_conv = """CREATE TABLE `conversatii` (
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
/*!40101 SET character_set_client = @saved_cs_client */;"""

replacement_conv = target_conv + """

--
-- Dumping data for table `conversatii`
--

LOCK TABLES `conversatii` WRITE;
/*!40000 ALTER TABLE `conversatii` DISABLE KEYS */;
INSERT INTO `conversatii` VALUES (5,'2026-06-07 18:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-06-07 20:35:00.000000','2026-06-07 20:35:00.000000');
/*!40000 ALTER TABLE `conversatii` ENABLE KEYS */;
UNLOCK TABLES;"""

target_mesaje = """CREATE TABLE `mesaje` (
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
/*!40101 SET character_set_client = @saved_cs_client */;"""

replacement_mesaje = target_mesaje + """

--
-- Dumping data for table `mesaje`
--

LOCK TABLES `mesaje` WRITE;
/*!40000 ALTER TABLE `mesaje` DISABLE KEYS */;
INSERT INTO `mesaje` VALUES (15,'2026-06-07 20:10:00.000000','Bună seara, domnule terapeut! Aș dori să reprogramez ședința de mâine (luni, 8 iunie) pentru marți, 9 iunie, dacă este posibil.',5,b'1','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','PACIENT','2026-06-07 18:00:00.000000'),(16,'2026-06-07 20:25:00.000000','Bună seara, Sabrina! Sigur că da. Te pot programa marți la ora 10:00. Este în regulă?',5,b'1','51abc54c-0c2f-4f0a-aead-bdaa9627661e','TERAPEUT','2026-06-07 20:15:00.000000'),(17,'2026-06-07 20:30:00.000000','Da, este perfect! Vă mulțumesc frumos.',5,b'1','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','PACIENT','2026-06-07 20:30:00.000000'),(18,'2026-06-07 20:36:00.000000','Cu drag! Am operat modificarea în sistem. Ne vedem marți.',5,b'1','51abc54c-0c2f-4f0a-aead-bdaa9627661e','TERAPEUT','2026-06-07 20:35:00.000000');
/*!40000 ALTER TABLE `mesaje` ENABLE KEYS */;
UNLOCK TABLES;"""

if target_conv in chat_content:
    chat_content = chat_content.replace(target_conv, replacement_conv)
else:
    print("Warning: target_conv not found in chat_service.sql")

if target_mesaje in chat_content:
    chat_content = chat_content.replace(target_mesaje, replacement_mesaje)
else:
    print("Warning: target_mesaje not found in chat_service.sql")

with open(chat_file, "w", encoding="utf-8") as f:
    f.write(chat_content)


# ==============================================================================
# 2. Update notificari_service.sql
# ==============================================================================
print("Updating notificari_service.sql...")
with open(notificari_file, "r", encoding="utf-8") as f:
    notif_content = f.read()

target_notif = ") ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;"

replacement_notif = target_notif + """

--
-- Dumping data for table `notificari`
--

LOCK TABLES `notificari` WRITE;
/*!40000 ALTER TABLE `notificari` DISABLE KEYS */;
INSERT INTO `notificari` VALUES (51,'2026-05-28 11:30:00.000000','2026-05-28 10:00:00.000000',101,b'1','O nouă programare a fost înregistrată pentru pacientul Sabrina Ene în data de 01.06.2026 la ora 10:00.','PROGRAMARE_NOUA','PROGRAMARE','TERAPEUT','Programare Noua','/terapeut/programari','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(52,'2026-06-07 20:15:00.000000','2026-06-07 18:00:00.000000',15,b'1','Ai primit un mesaj nou de la pacientul Sabrina Ene: "Bună seara, domnule terapeut! Aș dori..."','MESAJ_DE_LA_PACIENT','MESAJ','TERAPEUT','Mesaj Nou de la Pacient','/terapeut/chat?pacient=a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(53,'2026-06-07 20:16:00.000000','2026-06-07 20:35:00.000000',104,b'1','Programarea pacientului Sabrina Ene din data de 08.06.2026 a fost anulată de către pacient.','PROGRAMARE_ANULATA_DE_PACIENT','PROGRAMARE','TERAPEUT','Programare Anulată de Pacient','/terapeut/programari','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(54,'2026-06-02 08:30:00.000000','2026-06-01 18:00:00.000000',101,b'1','Pacientul Sabrina Ene a completat jurnalul de simptome pentru ședința din 01.06.2026.','JURNAL_COMPLETAT','JURNAL','TERAPEUT','Jurnal Simptome Completat','/terapeut/pacienti/a2351fcd-9e0c-4ebc-b6fb-2530c9892556/jurnal','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(55,'2026-06-11 08:30:00.000000','2026-06-10 11:05:00.000000',106,b'1','Cota de ședințe prescrise pentru Sabrina Ene a fost finalizată. Este necesară o reevaluare clinică.','REEVALUARE_NECESARA','PACIENT','TERAPEUT','Reevaluare Necesară','/terapeut/pacienti/a2351fcd-9e0c-4ebc-b6fb-2530c9892556/evaluari','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),(56,'2026-06-07 20:30:00.000000','2026-06-07 20:15:00.000000',16,b'1','Ai primit un mesaj nou de la terapeutul tău, Vlad Radu.','MESAJ_DE_LA_TERAPEUT','MESAJ','PACIENT','Mesaj Nou de la Terapeut','/pacient/chat','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),(57,'2026-06-12 11:30:00.000000','2026-06-12 10:35:00.000000',21,b'1','Terapeutul tău a finalizat reevaluarea clinică. A fost recomandat un nou plan de tratament (4 ședințe de stabilizare).','REEVALUARE_RECOMANDATA','EVALUARE','PACIENT','Plan Nou Recomandat','/pacient/plan-tratament','a2351fcd-9e0c-4ebc-b6fb-2530c9892556');
/*!40000 ALTER TABLE `notificari` ENABLE KEYS */;
UNLOCK TABLES;"""

if target_notif in notif_content:
    notif_content = notif_content.replace(target_notif, replacement_notif, 1) # Replace first occurrence
else:
    print("Warning: target_notif not found in notificari_service.sql")

with open(notificari_file, "w", encoding="utf-8") as f:
    f.write(notif_content)


# ==============================================================================
# 3. Update pacienti_service.sql
# ==============================================================================
print("Updating pacienti_service.sql...")
with open(pacienti_file, "r", encoding="utf-8") as f:
    pac_content = f.read()

target_jurnal_end = "/*!40000 ALTER TABLE `jurnal_pacient` ENABLE KEYS */;"
new_jurnal_inserts = """INSERT INTO `jurnal_pacient` (`id`, `comentarii`, `created_at`, `data`, `dificultate_exercitii`, `nivel_durere`, `nivel_oboseala`, `pacient_id`, `programare_id`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
(101,'Usoara durere in zona cefei la inceputul exercitiilor, dar a trecut pe parcursul programului.','2026-06-01 18:00:00.000000','2026-06-01',4,3,4,18,101,'2026-06-01 18:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),
(102,'Exercitiile posturare au fost solicitante astazi. Ma simt mai bine decat saptamana trecuta.','2026-06-03 19:30:00.000000','2026-06-03',5,2,5,18,102,'2026-06-03 19:30:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),
(103,'Senzatie placuta de relaxare la nivelul umerilor si spatelui la finalul sedintei.','2026-06-05 18:15:00.000000','2026-06-05',3,1,3,18,103,'2026-06-05 18:15:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),
(104,'M-am simtit destul de obosita dupa munca, dar sedinta m-a ajutat sa ma detensionez.','2026-06-09 19:00:00.000000','2026-06-09',4,2,6,18,105,'2026-06-09 19:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),
(105,'Ultima sedinta a fost geniala. Durerea mea a disparut complet. Foarte multumita de rezultate.','2026-06-10 18:45:00.000000','2026-06-10',2,0,3,18,106,'2026-06-10 18:45:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),
(106,'Evaluarea a fost corecta, am discutat cu placere noul plan terapeutic.','2026-06-12 17:00:00.000000','2026-06-12',2,0,2,18,107,'2026-06-12 17:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),
(107,'Inceput bun de ciclu nou. Exercitiile pentru core au fost destul de grele dar distractive.','2026-07-06 18:30:00.000000','2026-07-06',6,1,4,18,108,'2026-07-06 18:30:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),
(108,'Simt musculatura spatelui obosita dar stabila, fara niciun junghi sau jena.','2026-07-08 19:15:00.000000','2026-07-08',4,0,3,18,109,'2026-07-08 19:15:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),
(109,'Excelenta sedinta pe mingea de pilates si pe pernele instabile. Foarte buna coordonare.','2026-07-10 18:00:00.000000','2026-07-10',3,0,3,18,110,'2026-07-10 18:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),
(110,'Am finalizat cu succes programamul de fizioterapie. Ma simt complet vindecata si stapana pe mine.','2026-07-15 19:00:00.000000','2026-07-15',2,0,2,18,112,'2026-07-15 19:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),
(111,'Evaluare amanuntita. Am dureri mari la aplecare, dar am speranta ca kinetoterapia ma va ajuta.','2026-06-01 17:30:00.000000','2026-06-01',3,7,5,8,113,'2026-06-01 17:30:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(112,'Dupa prima sedinta am o febra musculara destul de puternica. Durerea de spate e constanta.','2026-06-03 18:00:00.000000','2026-06-03',5,6,6,8,114,'2026-06-03 18:00:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(113,'Exercitiile din patrupedie imi aduc o usurare pe moment. Terapeutul este foarte atent.','2026-06-05 18:30:00.000000','2026-06-05',4,4,5,8,115,'2026-06-05 18:30:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(114,'Durerea a inceput sa se mute dintr-o parte in alta, dar intensitatea a scazut la 3. Spatele e mai flexibil.','2026-06-08 19:00:00.000000','2026-06-08',4,3,4,8,116,'2026-06-08 19:00:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(115,'Ma simt tot mai energica dupa fiecare antrenament medical. Nu mai am acea frica de aplecare.','2026-06-10 18:15:00.000000','2026-06-10',3,2,3,8,117,'2026-06-10 18:15:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(116,'Am reusit sa stau la birou 4 ore fara sa simt disconfort lombar acut. Progres urias pentru mine.','2026-06-12 18:00:00.000000','2026-06-12',3,2,3,8,118,'2026-06-12 18:00:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(117,'Ultima sedinta din primul calup. Spatele meu se simte extrem de usor si eliberat de presiune.','2026-06-15 19:30:00.000000','2026-06-15',2,1,3,8,119,'2026-06-15 19:30:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(118,'Reevaluare extrem de profesionista. Am stabilit obiectivele pentru perioada de mentinere profilactica.','2026-06-17 17:00:00.000000','2026-06-17',2,1,2,8,120,'2026-06-17 17:00:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(119,'Gimnastica profilactica este excelenta. Foarte focusata pe mobilitate si intindere musculara.','2026-07-06 18:00:00.000000','2026-07-06',3,1,3,8,121,'2026-07-06 18:00:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(120,'Sedinta dinamica foarte revigoranta. Simt ca stau natural mult mai dreapta acum.','2026-07-08 19:00:00.000000','2026-07-08',3,0,3,8,122,'2026-07-08 19:00:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(121,'Stretching profund extrem de benefic la sfarsit de saptamana. Tensiunea din zona umerilor a disparut.','2026-07-10 17:45:00.000000','2026-07-10',2,0,2,8,123,'2026-07-10 17:45:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(122,'Ultima sedinta inainte de a pleca in concediu. Multumesc din suflet pentru programul personalizat.','2026-07-13 18:30:00.000000','2026-07-13',2,0,2,8,124,'2026-07-13 18:30:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(123,'Evaluare riguroasa post-operatorie. Simt ca sunt pe maini bune cu noul terapeut.','2026-06-01 17:00:00.000000','2026-06-01',3,8,5,11,126,'2026-06-01 17:00:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','8bd48465-32ec-413b-9985-9a57a03a3d88'),
(124,'Prima sedinta de exercitii post-op. Rigiditatea din zona lombara a fost mare, dar durerea n-a depasit suportabilul.','2026-06-02 18:30:00.000000','2026-06-02',6,6,6,11,127,'2026-06-02 18:30:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','8bd48465-32ec-413b-9985-9a57a03a3d88'),
(125,'Decomprimarea coloanei din cadrul sedintei din Locatia 8 a fost fantastica. Iradierea de pe picior s-a retras.','2026-06-04 19:00:00.000000','2026-06-04',5,4,5,11,128,'2026-06-04 19:00:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','8bd48465-32ec-413b-9985-9a57a03a3d88'),
(126,'Mobilitatea generala creste de la o sedinta la alta. Frica de miscare a disparut in mare parte.','2026-06-05 18:00:00.000000','2026-06-05',4,3,4,11,129,'2026-06-05 18:00:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','8bd48465-32ec-413b-9985-9a57a03a3d88'),
(127,'Fesierii si abdomenul au lucrat serios astazi. Spatele se simte bine consolidat.','2026-06-08 18:45:00.000000','2026-06-08',5,2,5,11,130,'2026-06-08 18:45:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','8bd48465-32ec-413b-9985-9a57a03a3d88'),
(128,'Reevaluarea de azi mi-a aratat cifre clare: progres de 80% pe mobilitate si forta.','2026-06-09 17:30:00.000000','2026-06-09',2,1,3,11,131,'2026-06-09 17:30:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','8bd48465-32ec-413b-9985-9a57a03a3d88'),
(129,'Am revenit in program cu Vlad. Programul de core-stability este intens dar benefic.','2026-07-08 18:30:00.000000','2026-07-08',5,1,4,11,133,'2026-07-08 18:30:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','8bd48465-32ec-413b-9985-9a57a03a3d88'),
(130,'Placa de echilibru a fost o adevarata provocare azi, insa controlul meu postural e net superior.','2026-07-10 19:15:00.000000','2026-07-10',5,0,4,11,134,'2026-07-10 19:15:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','8bd48465-32ec-413b-9985-9a57a03a3d88'),
(131,'Fara absolut nicio jena sau durere, chiar si in momentele de suprasolicitare dinamica.','2026-07-13 18:00:00.000000','2026-07-13',3,0,3,11,135,'2026-07-13 18:00:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','8bd48465-32ec-413b-9985-9a57a03a3d88'),
(132,'Recuperare incheiata cu succes. Ma simt excelent, spatele este complet functional.','2026-07-15 19:30:00.000000','2026-07-15',2,0,2,11,136,'2026-07-15 19:30:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','8bd48465-32ec-413b-9985-9a57a03a3d88');
""" + target_jurnal_end

if target_jurnal_end in pac_content:
    pac_content = pac_content.replace(target_jurnal_end, new_jurnal_inserts)
else:
    print("Warning: target_jurnal_end not found in pacienti_service.sql")

with open(pacienti_file, "w", encoding="utf-8") as f:
    f.write(pac_content)


# ==============================================================================
# 4. Update programari_service.sql
# ==============================================================================
print("Updating programari_service.sql...")
with open(programari_file, "r", encoding="utf-8") as f:
    prog_content = f.read()

# Tables to update: evaluari, evolutii, programari, relatie_pacient_terapeut

# A. Evaluari
target_evaluari_end = "/*!40000 ALTER TABLE `evaluari` ENABLE KEYS */;"
new_evaluari_inserts = """INSERT INTO `evaluari` (`id`, `created_at`, `data`, `diagnostic`, `observatii`, `pacient_keycloak_id`, `programare_id`, `sedinte_recomandate`, `serviciu_recomandat_id`, `terapeut_keycloak_id`, `tip`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
(21,'2026-06-12 10:35:00.000000','2026-06-12','Cervicalgie miotensivă - Ameliorată rapid','Mobilitatea cervicala este complet restaurata, fara dureri la miscarile de rotatie. Spasmele pe trapez s-au redus considerabil. Recomand 4 sedinte individuale de stabilizare posturala pentru preventie.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',107,4,6,'51abc54c-0c2f-4f0a-aead-bdaa9627661e','REEVALUARE','2026-06-12 10:35:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(22,'2026-06-01 09:35:00.000000','2026-06-01','Lombalgie cronică','Pacienta acuza dureri lombare difuze, accentuate dupa perioade lungi de sezut la birou. Mobilitatea coloanei este redusa in flexie. Forta musculara abdominala este scazuta. Recomand un program de 6 sedinte individuale axate pe decompresie si tonifierea musculaturii paravertebrale.','de85c053-a583-4219-8fe9-5afd66c0e812',113,6,6,'d407ab1c-7554-456d-9726-3f5571fa8fd0','INITIALA','2026-06-01 09:35:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(23,'2026-06-17 09:35:00.000000','2026-06-17','Lombalgie cronică - Ameliorată','Durerea a scazut semnificativ (de la 7 la 1). Controlul motor al bazinului s-a imbunatatit. Recomand continuarea cu 5 sedinte de gimnastica profilactica pentru mentinere si educatie posturala.','de85c053-a583-4219-8fe9-5afd66c0e812',120,5,8,'d407ab1c-7554-456d-9726-3f5571fa8fd0','REEVALUARE','2026-06-17 09:35:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(24,'2026-06-01 11:35:00.000000','2026-06-01','Hernie de disc L5-S1 (post-op)','Pacienta se prezinta la 6 saptamani post-operator. Prezinta frica de miscare (kineziofobie), rigiditate lombara si deficit de forta pe membrul inferior stang. Se recomanda 4 sedinte de recuperare post-operatorie focusate pe decompresie blanda si recastigarea mobilitatii neuro-musculare.','8bd48465-32ec-413b-9985-9a57a03a3d88',126,4,7,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','INITIALA','2026-06-01 11:35:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(25,'2026-06-09 11:35:00.000000','2026-06-09','Hernie de disc L5-S1 (post-op) - Evoluție pozitivă','Mobilitatea generala s-a imbunatatit. Kineziofobia s-a redus substantial. Pacienta executa tranzitii posturale corecte. Recomand un nou program de 4 sedinte individuale de kinetoterapie pentru stabilizare activa.','8bd48465-32ec-413b-9985-9a57a03a3d88',131,4,6,'05e0f10e-2c4d-405e-abac-5ccedb83d2af','REEVALUARE','2026-06-09 11:35:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af');
""" + target_evaluari_end

if target_evaluari_end in prog_content:
    prog_content = prog_content.replace(target_evaluari_end, new_evaluari_inserts)
else:
    print("Warning: target_evaluari_end not found")

# B. Evolutii
target_evolutii_end = "/*!40000 ALTER TABLE `evolutii` ENABLE KEYS */;"
new_evolutii_inserts = """INSERT INTO `evolutii` (`id`, `created_at`, `observatii`, `pacient_keycloak_id`, `terapeut_keycloak_id`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
(21,'2026-06-01 11:05:00.000000','Prima sedinta de kinetoterapie. Am lucrat pe mobilitate articulara pasiva si stretching bland. Pacienta are o usoara teama de durere, dar raspunde bine la indicatii.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-06-01 11:05:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(22,'2026-06-03 11:05:00.000000','Am introdus exercitii izometrice pentru stabilizare paravertebrala si cervicala. Contracturile musculare din zona omoplatilor incep sa se relaxeze.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-06-03 11:05:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(23,'2026-06-05 11:05:00.000000','Mobilitatea cervicala a progresat. Pacienta raporteaza ca durerea matinala a scazut in intensitate. Am crescut numarul de repetari la exercitiile posturale.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-06-05 11:05:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(24,'2026-06-09 11:05:00.000000','Sedinta reprogramata. Pacienta a fost obosita, dar am putut efectua programul complet. Control postural bun la oglinda.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-06-09 11:05:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(25,'2026-06-10 11:05:00.000000','Sedinta 5 finalizata. Cota prescribed atinsa. Pacienta are o postura mult imbunatatita, durerea este aproape absenta. Urmeaza reevaluarea programata.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-06-10 11:05:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(26,'2026-07-06 10:55:00.000000','Inceputul etapei de stabilizare activa (Ciclul II). Am lucrat pe core-stability si exercitii cu banda elastica pentru umeri. Fara simptome dureroase.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-06 10:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(27,'2026-07-08 10:55:00.000000','Echilibru bun la exercitiile pe suprafete instabile. Corectii minime necesare pe alinierea trunchiului.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-08 10:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(28,'2026-07-10 10:55:00.000000','Toleranta mare la efort. Pacienta efectueaza si acasa planul de intretinere. Postura stabila.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-10 10:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(29,'2026-07-15 10:55:00.000000','Sedinta 4 finalizata. Obiectivele clinice au fost indeplinite integral. Pacienta este externata cu recomandari clare de mentinere pe termen lung.','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-15 10:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(30,'2026-06-03 09:55:00.000000','Prima sedinta de kinetoterapie pentru lombalgie. Mobilizari blande pe flexie/extensie si exercitii usoare de respiratie. Durere locala declarata la efort.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-06-03 09:55:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(31,'2026-06-05 09:55:00.000000','Contractura paravertebrala incepe sa se diminueze. Am inceput tonifierea musculaturii abdominale profunde (transversul abdominal).','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-06-05 09:55:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(32,'2026-06-08 09:55:00.000000','Pacienta raporteaza ameliorarea semnificativa a simptomelor la birou. Am adaugat exercitii din pozitia patrupedie.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-06-08 09:55:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(33,'2026-06-10 09:55:00.000000','Executie buna a exercitiilor de tip "plank" adaptate. Rezistenta musculara crescuta pe zona lombara.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-06-10 09:55:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(34,'2026-06-12 09:55:00.000000','Fara episoade de durere acuta in ultima saptamana. Lucram intens pe mobilitatea soldurilor si stretching active pe lantul posterior.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-06-12 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(35,'2026-06-15 09:55:00.000000','Ultima sedinta din planul initial. Pacienta descrie o stare generala excelenta, avand mobilitate completa si absenta durerilor zilnice.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-06-15 09:55:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(36,'2026-07-06 09:55:00.000000','Inceperea sedintelor de gimnastica profilactica (mentinere). Focusul este mentinerea tonusului si constientizarea posturii corecte la birou.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-07-06 09:55:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(37,'2026-07-08 09:55:00.000000','Exercitii dinamice executate cu usurinta. Pacienta demonstreaza insusirea corecta a tehnicilor de ridicare a greutatilor din viata cotidiana.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-07-08 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(38,'2026-07-10 09:55:00.000000','Stretching intens al musculaturii flexoare de sold si pectorale. Postura globala deschisa, fara cifotizare.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-07-10 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(39,'2026-07-13 09:55:00.000000','Ultima sedinta din programul de mentinere. Pacientul relateaza ca se simte excelent, fara dureri. Recomandari de mentinere oferite.','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-07-13 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(40,'2026-06-02 12:05:00.000000','Prima sedinta post-operatorie. Exercitii foarte usoare in descarcare de greutate pe saltea. Pacienta prezinta rigiditate musculara, dar este foarte cooperanta.','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-06-02 12:05:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(41,'2026-06-04 16:05:00.000000','Sedinta desfasurata in Locatia 8. S-au efectuat mobilizari neuro-dinamice pentru nervul sciatic. Durerea iradiata pe picior s-a diminuat considerabil.','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-06-04 16:05:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(42,'2026-06-05 16:05:00.000000','Continuam cresterea amplitudinii de miscare in extensie si rotatie. Forta musculara pe cvadriceps si fesieri se imbunatateste treptat.','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-06-05 16:05:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(43,'2026-06-08 12:05:00.000000','Ultima sedinta din planul post-operator. Control motor optim pe trunchi. Amplitudinea articulara este sigura si stabile. Pacienta este gata pentru reevaluare.','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-06-08 12:05:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(44,'2026-07-08 11:55:00.000000','Pacienta a revenit in programul meu pentru faza de stabilizare activa. Am initiat exercitii specifice de tip core-stability si exercitii pe placa de echilibru.','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-08 11:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(45,'2026-07-10 11:55:00.000000','Rezistenta musculara crescuta la nivelul trunchiului. Corectii posturale minime, pacienta constientizeaza instantaneu abaterile de aliniere.','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-10 11:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(46,'2026-07-13 11:55:00.000000','Executie fara ezitari la exercitiile cu rezistenta elastica mare. Absenta totala a durerilor la activitatile zilnice de birou.','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-13 11:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(47,'2026-07-15 11:55:00.000000','Ultima sedinta finalizata. Faza de stabilizare post-operatorie incheiata cu succes. Pacienta prezinta autonomie completa si a primit planul de auto-intretinere.','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-15 11:55:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e');
""" + target_evolutii_end

if target_evolutii_end in prog_content:
    prog_content = prog_content.replace(target_evolutii_end, new_evolutii_inserts)
else:
    print("Warning: target_evolutii_end not found")

# C. Programari
target_programari_end = "/*!40000 ALTER TABLE `programari` ENABLE KEYS */;"
new_programari_inserts = """INSERT INTO `programari` (`id`, `are_evaluare`, `are_jurnal`, `created_at`, `data`, `durata_minute`, `locatie_id`, `motiv_anulare`, `ora_inceput`, `ora_sfarsit`, `pacient_keycloak_id`, `pret`, `prima_intalnire`, `serviciu_id`, `status`, `terapeut_keycloak_id`, `tip_serviciu`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
(101,b'0',b'1','2026-05-28 10:00:00.000000','2026-06-01',60,2,NULL,'10:00:00.000000','11:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',150.00,b'0',7,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Recuperare post-operatorie','2026-06-01 11:05:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(102,b'0',b'1','2026-05-28 10:05:00.000000','2026-06-03',60,2,NULL,'10:00:00.000000','11:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',150.00,b'0',7,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Recuperare post-operatorie','2026-06-03 11:05:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(103,b'0',b'1','2026-05-28 10:10:00.000000','2026-06-05',60,2,NULL,'10:00:00.000000','11:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',150.00,b'0',7,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Recuperare post-operatorie','2026-06-05 11:05:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(104,b'0',b'0','2026-05-28 10:15:00.000000','2026-06-08',60,2,'ANULAT_DE_PACIENT','10:00:00.000000','11:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',150.00,b'0',7,'ANULATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Recuperare post-operatorie','2026-06-07 20:35:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),
(105,b'0',b'1','2026-06-07 20:35:00.000000','2026-06-09',60,2,NULL,'10:00:00.000000','11:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',150.00,b'0',7,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Recuperare post-operatorie','2026-06-09 11:05:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(106,b'0',b'1','2026-05-28 10:20:00.000000','2026-06-10',60,2,NULL,'10:00:00.000000','11:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',150.00,b'0',7,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Recuperare post-operatorie','2026-06-10 11:05:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(107,b'1',b'1','2026-06-10 11:15:00.000000','2026-06-12',30,2,NULL,'10:00:00.000000','10:30:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',100.00,b'0',2,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Evaluare - Reevaluare','2026-06-12 10:35:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(108,b'0',b'1','2026-06-12 11:00:00.000000','2026-07-06',50,2,NULL,'10:00:00.000000','10:50:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-06 10:55:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(109,b'0',b'1','2026-06-12 11:05:00.000000','2026-07-08',50,2,NULL,'10:00:00.000000','10:50:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-08 10:55:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(110,b'0',b'1','2026-06-12 11:10:00.000000','2026-07-10',50,2,NULL,'10:00:00.000000','10:50:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-10 10:55:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(111,b'0',b'0','2026-06-12 11:15:00.000000','2026-07-13',50,2,'ANULAT_DE_PACIENT','10:00:00.000000','10:50:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',100.00,b'0',6,'ANULATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-12 18:00:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','a2351fcd-9e0c-4ebc-b6fb-2530c9892556'),
(112,b'0',b'1','2026-07-12 18:15:00.000000','2026-07-15',50,2,NULL,'10:00:00.000000','10:50:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-15 10:55:00.000000','a2351fcd-9e0c-4ebc-b6fb-2530c9892556','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(113,b'1',b'1','2026-05-28 12:00:00.000000','2026-06-01',30,13,NULL,'09:00:00.000000','09:30:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',150.00,b'1',1,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Evaluare - Evaluare Inițială','2026-06-01 09:35:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(114,b'0',b'1','2026-06-01 09:45:00.000000','2026-06-03',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',100.00,b'0',6,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Ședință individuală','2026-06-03 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(115,b'0',b'1','2026-06-01 09:50:00.000000','2026-06-05',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',100.00,b'0',6,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Ședință individuală','2026-06-05 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(116,b'0',b'1','2026-06-01 09:55:00.000000','2026-06-08',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',100.00,b'0',6,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Ședință individuală','2026-06-08 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(117,b'0',b'1','2026-06-01 10:00:00.000000','2026-06-10',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',100.00,b'0',6,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Ședință individuală','2026-06-10 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(118,b'0',b'1','2026-06-01 10:05:00.000000','2026-06-12',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',100.00,b'0',6,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Ședință individuală','2026-06-12 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(119,b'0',b'1','2026-06-01 10:10:00.000000','2026-06-15',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',100.00,b'0',6,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Ședință individuală','2026-06-15 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(120,b'1',b'1','2026-06-15 10:00:00.000000','2026-06-17',30,13,NULL,'09:00:00.000000','09:30:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',100.00,b'0',2,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Evaluare - Reevaluare','2026-06-17 09:35:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(121,b'0',b'1','2026-06-17 09:40:00.000000','2026-07-06',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',110.00,b'0',8,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Gimnastică profilactică','2026-07-06 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(122,b'0',b'1','2026-06-17 09:45:00.000000','2026-07-08',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',110.00,b'0',8,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Gimnastică profilactică','2026-07-08 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(123,b'0',b'1','2026-06-17 09:50:00.000000','2026-07-10',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',110.00,b'0',8,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Gimnastică profilactică','2026-07-10 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(124,b'0',b'1','2026-06-17 09:55:00.000000','2026-07-13',50,13,NULL,'09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',110.00,b'0',8,'FINALIZATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Gimnastică profilactică','2026-07-13 09:55:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(125,b'0',b'0','2026-06-17 10:00:00.000000','2026-07-15',50,13,'ANULAT_DE_PACIENT','09:00:00.000000','09:50:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812',110.00,b'0',8,'ANULATA','d407ab1c-7554-456d-9726-3f5571fa8fd0','Kinetoterapie - Gimnastică profilactică','2026-07-14 15:30:00.000000','de85c053-a583-4219-8fe9-5afd66c0e812','de85c053-a583-4219-8fe9-5afd66c0e812'),
(126,b'1',b'1','2026-05-28 14:00:00.000000','2026-06-01',30,7,NULL,'11:00:00.000000','11:30:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',150.00,b'1',1,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Evaluare Inițială','2026-06-01 11:35:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(127,b'0',b'1','2026-06-01 11:45:00.000000','2026-06-02',60,7,NULL,'11:00:00.000000','12:00:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',150.00,b'0',7,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Recuperare post-operatorie','2026-06-02 12:05:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(128,b'0',b'1','2026-06-01 11:50:00.000000','2026-06-04',60,8,NULL,'15:00:00.000000','16:00:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',150.00,b'0',7,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Recuperare post-operatorie','2026-06-04 16:05:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(129,b'0',b'1','2026-06-01 11:55:00.000000','2026-06-05',60,8,NULL,'15:00:00.000000','16:00:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',150.00,b'0',7,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Recuperare post-operatorie','2026-06-05 16:05:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(130,b'0',b'1','2026-06-01 12:00:00.000000','2026-06-08',60,7,NULL,'11:00:00.000000','12:00:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',150.00,b'0',7,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Recuperare post-operatorie','2026-06-08 12:05:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(131,b'1',b'1','2026-06-08 12:15:00.000000','2026-06-09',30,7,NULL,'11:00:00.000000','11:30:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',100.00,b'0',2,'FINALIZATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Evaluare - Reevaluare','2026-06-09 11:35:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(132,b'0',b'0','2026-06-09 12:00:00.000000','2026-06-15',60,7,'ANULAT_DE_TERAPEUT','11:00:00.000000','12:00:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',150.00,b'0',7,'ANULATA','05e0f10e-2c4d-405e-abac-5ccedb83d2af','Kinetoterapie - Recuperare post-operatorie','2026-06-14 09:00:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(133,b'0',b'1','2026-07-01 10:30:00.000000','2026-07-08',50,2,NULL,'11:00:00.000000','11:50:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-08 11:55:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(134,b'0',b'1','2026-07-01 10:35:00.000000','2026-07-10',50,2,NULL,'11:00:00.000000','11:50:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-10 11:55:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(135,b'0',b'1','2026-07-01 10:40:00.000000','2026-07-13',50,2,NULL,'11:00:00.000000','11:50:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-13 11:55:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e'),
(136,b'0',b'1','2026-07-01 10:45:00.000000','2026-07-15',50,2,NULL,'11:00:00.000000','11:50:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88',100.00,b'0',6,'FINALIZATA','51abc54c-0c2f-4f0a-aead-bdaa9627661e','Kinetoterapie - Ședință individuală','2026-07-15 11:55:00.000000','8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e');
""" + target_programari_end

if target_programari_end in prog_content:
    prog_content = prog_content.replace(target_programari_end, new_programari_inserts)
else:
    print("Warning: target_programari_end not found")

# D. Relatie Pacient Terapeut
target_relatie_end = "/*!40000 ALTER TABLE `relatie_pacient_terapeut` ENABLE KEYS */;"
new_relatie_inserts = """INSERT INTO `relatie_pacient_terapeut` (`id`, `activa`, `created_at`, `data_inceput`, `data_sfarsit`, `pacient_keycloak_id`, `terapeut_keycloak_id`, `updated_at`, `created_by`, `last_modified_by`) VALUES 
(21,b'1','2026-06-01 09:00:00.000000','2026-06-01',NULL,'de85c053-a583-4219-8fe9-5afd66c0e812','d407ab1c-7554-456d-9726-3f5571fa8fd0','2026-06-01 09:00:00.000000','d407ab1c-7554-456d-9726-3f5571fa8fd0','d407ab1c-7554-456d-9726-3f5571fa8fd0'),
(22,b'1','2026-06-01 11:00:00.000000','2026-06-01',NULL,'8bd48465-32ec-413b-9985-9a57a03a3d88','05e0f10e-2c4d-405e-abac-5ccedb83d2af','2026-06-01 11:00:00.000000','05e0f10e-2c4d-405e-abac-5ccedb83d2af','05e0f10e-2c4d-405e-abac-5ccedb83d2af'),
(23,b'1','2026-07-01 10:00:00.000000','2026-07-01',NULL,'8bd48465-32ec-413b-9985-9a57a03a3d88','51abc54c-0c2f-4f0a-aead-bdaa9627661e','2026-07-01 10:00:00.000000','51abc54c-0c2f-4f0a-aead-bdaa9627661e','51abc54c-0c2f-4f0a-aead-bdaa9627661e');

-- Actualizari de relatii vechi
UPDATE `relatie_pacient_terapeut` SET `activa` = b'0', `data_sfarsit` = '2026-06-01', `updated_at` = '2026-06-01 09:00:00.000000', `last_modified_by` = 'd407ab1c-7554-456d-9726-3f5571fa8fd0' WHERE `id` = 8;
UPDATE `relatie_pacient_terapeut` SET `activa` = b'0', `data_sfarsit` = '2026-06-01', `updated_at` = '2026-06-01 11:00:00.000000', `last_modified_by` = '05e0f10e-2c4d-405e-abac-5ccedb83d2af' WHERE `id` = 10;
UPDATE `relatie_pacient_terapeut` SET `activa` = b'0', `data_sfarsit` = '2026-07-01', `updated_at` = '2026-07-01 10:00:00.000000', `last_modified_by` = '51abc54c-0c2f-4f0a-aead-bdaa9627661e' WHERE `id` = 22;
""" + target_relatie_end

if target_relatie_end in prog_content:
    prog_content = prog_content.replace(target_relatie_end, new_relatie_inserts)
else:
    print("Warning: target_relatie_end not found")

with open(programari_file, "w", encoding="utf-8") as f:
    f.write(prog_content)

print("Mock data successfully injected into all db-init SQL files.")
