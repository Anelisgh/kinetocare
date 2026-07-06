## Secțiunea 11: Reziliență și Gestionarea Erorilor (Resilience & Error Handling)
Pentru a asigura o experiență excelentă pentru utilizatori și dezvoltatori, KinetoCare standardizează comunicarea erorilor în întregul sistem distribuit.
### 11.1 Problem Details conform standardului RFC 9457 (`BaseGlobalExceptionHandler`)
În loc de a returna stack trace-uri brute sau payload-uri JSON neuniforme, toate microserviciile moștenesc un `@RestControllerAdvice` centralizat care respectă specificațiile **RFC 9457 (Problem Details for HTTP APIs)** prin intermediul clasei `ProblemDetail` din Spring 6.
Clasa: `com.example.common.exception.BaseGlobalExceptionHandler`
1. **Excepții de Domeniu (Domain Exceptions)**: Excepțiile personalizate, cum ar fi `ResourceNotFoundException` (404) sau `ForbiddenOperationException` (403), sunt mapate curat pe coduri de status HTTP specifice, având titluri ușor de înțeles de către oameni.
2. **Erori de Validare (Validation Errors)**: Atunci când un DTO eșuează la validarea constrângerilor `@Valid` (`MethodArgumentNotValidException`), handler-ul interceptează obiectul `BindingResult` și populează o hartă personalizată `erori_campuri`:
    ```json
    {
      "type": "about:blank",
      "title": "Validare Eșuată",
      "status": 400,
      "detail": "Datele introduse nu sunt valide.",
      "erori_campuri": {
        "cnp": "CNP-ul trebuie să aibă 13 caractere",
        "telefon": "Numărul de telefon este invalid"
      }
    }
    ```
    Această structură JSON exactă este cea așteptată și analizată de `api.js` pe frontend.
### 11.2 Toleranța la Erori Inter-Servicii (Feign)
Când microserviciul A apelează microserviciul B prin Feign, iar B aruncă o excepție (e.g., 404 Not Found), comportamentul implicit al Feign este de a arunca o excepție generică `FeignException`. Acest lucru distruge contextul de business.
Clasa: `CustomErrorDecoder` (implementează `feign.codec.ErrorDecoder`)
Pentru a păstra semantica domeniului, fiecare serviciu configurează un decodificator personalizat de erori. Acesta interceptează obiectul brut `feign.Response`, citește statusul HTTP și aruncă o excepție de domeniu puternic tipizată:
- `404` -> Aruncă `ResourceNotFoundException`.
- `400` -> Aruncă `ExternalServiceException("Eroare de validare...")`.
- `503` -> Aruncă `ExternalServiceException("Serviciul extern este indisponibil")`.
Acest lucru previne erorile în cascadă și permite serviciului apelant să gestioneze elegant eroarea sau să declanșeze o tranzacție compensatorie (rollback).
### 11.3 Granițele de Erori din Frontend și Interceptorii Axios
Frontend-ul reflectă reziliența de pe backend prin intermediul a două mecanisme principale:
1. **Axios Interceptors (`api.js`)**: Așa cum s-a detaliat în Secțiunea 9, erorile de tip `401` declanșează o reîmprospătare silențioasă a tokenului (silent refresh), în timp ce erorile `403` declanșează o redirecționare centralizată către ruta `/unauthorized`, ocolind logica de la nivelul componentelor. Mai mult, dacă serverul este complet oprit (Eroare de Rețea), se generează un obiect `AppError` pentru ca interfața să nu se prăbușească.
2. **Granițe de Erori React (React Error Boundaries)**: Arborii de componente critici sunt înveliți în componente React de tipul Error Boundary. Dacă o componentă React aruncă o eroare la randare (e.g., din cauza unor date malformate primite de la API), Error Boundary prinde prăbușirea, demontează arborele defect și randează o interfață sigură de fallback, asigurând funcționarea restului aplicației SPA.
