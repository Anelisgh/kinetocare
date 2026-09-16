# Anexă: Contracte API REST

| Metodă | Endpoint | Microserviciu | Rol Necesitat |
|---|---|---|---|
| `POST` | `/api/users/auth/register` | `user-service` | Public (Oricine) |
| `POST` | `/api/users/auth/login` | `user-service` | Public (Oricine) |
| `GET` | `/api/users` | `user-service` | `ROLE_ADMIN` |
| `PUT` | `/api/users/{id}/status` | `user-service` | `ROLE_ADMIN` |
| `GET` | `/api/terapeut` | `terapeuti-service` | `ROLE_TERAPEUT` |
| `POST` | `/api/terapeut` | `terapeuti-service` | `ROLE_TERAPEUT` |
| `GET` | `/api/terapeuti` | `terapeuti-service` | `ROLE_PACIENT`, `ROLE_ADMIN` |
| `GET` | `/api/terapeuti/{id}` | `terapeuti-service` | `ROLE_PACIENT`, `ROLE_TERAPEUT`, `ROLE_ADMIN` |
| `GET` | `/api/terapeut/statistici` | `terapeuti-service` | `ROLE_TERAPEUT` |
| `POST` | `/api/disponibilitate` | `terapeuti-service` | `ROLE_TERAPEUT` |
| `POST` | `/api/concediu` | `terapeuti-service` | `ROLE_TERAPEUT` |
| `GET` | `/api/locatii` | `terapeuti-service` | `ROLE_PACIENT`, `ROLE_TERAPEUT`, `ROLE_ADMIN` |
| `POST` | `/api/locatii` | `terapeuti-service` | `ROLE_ADMIN` |
| `PUT` | `/api/locatii/{id}` | `terapeuti-service` | `ROLE_ADMIN` |
| `GET` | `/api/pacient` | `pacienti-service` | `ROLE_PACIENT` |
| `POST` | `/api/pacient` | `pacienti-service` | `ROLE_PACIENT` |
| `GET` | `/api/jurnal` | `pacienti-service` | `ROLE_PACIENT` |
| `POST` | `/api/jurnal` | `pacienti-service` | `ROLE_PACIENT` |
| `POST` | `/api/programari` | `programari-service` | `ROLE_PACIENT` |
| `GET` | `/api/programari/pacient` | `programari-service` | `ROLE_PACIENT` |
| `GET` | `/api/programari/terapeut` | `programari-service` | `ROLE_TERAPEUT` |
| `PUT` | `/api/programari/{id}/status` | `programari-service` | `ROLE_TERAPEUT` |
| `GET` | `/api/programari/statistici` | `programari-service` | `ROLE_ADMIN` |
| `GET` | `/api/relatii` | `programari-service` | `ROLE_PACIENT`, `ROLE_TERAPEUT` |
| `GET` | `/api/fisa-pacient` | `programari-service` | `ROLE_TERAPEUT` |
| `GET` | `/api/evolutii` | `programari-service` | `ROLE_TERAPEUT` |
| `POST` | `/api/evaluari` | `programari-service` | `ROLE_TERAPEUT` |
| `GET` | `/api/servicii` | `servicii-service` | `ROLE_PACIENT`, `ROLE_TERAPEUT`, `ROLE_ADMIN` |
| `POST` | `/api/servicii` | `servicii-service` | `ROLE_ADMIN` |
| `PUT` | `/api/servicii/{id}` | `servicii-service` | `ROLE_ADMIN` |
| `DELETE` | `/api/servicii/{id}` | `servicii-service` | `ROLE_ADMIN` |
| `GET` | `/api/chat/conversatii` | `chat-service` | `ROLE_PACIENT`, `ROLE_TERAPEUT` |
| `GET` | `/api/chat/conversatii/{id}/mesaje` | `chat-service` | `ROLE_PACIENT`, `ROLE_TERAPEUT` |
| `GET` | `/api/notificari` | `notificari-service` | `ROLE_PACIENT`, `ROLE_TERAPEUT`, `ROLE_ADMIN` |
| `PUT` | `/api/notificari/{id}/citit` | `notificari-service` | `ROLE_PACIENT`, `ROLE_TERAPEUT`, `ROLE_ADMIN` |
