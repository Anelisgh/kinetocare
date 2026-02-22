package com.example.api_gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resursă negăsită: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Resursa nu a fost găsită");
        return problemDetail;
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ProblemDetail handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        log.warn("Conflict de resursă: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Resursa există deja");
        return problemDetail;
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ProblemDetail handleForbiddenOperationException(ForbiddenOperationException ex) {
        log.warn("Operațiune interzisă: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("Operațiune interzisă");
        return problemDetail;
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ProblemDetail handleExternalServiceException(ExternalServiceException ex) {
        log.error("Eroare la apelul către un serviciu extern: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, "Serviciul dependent este indisponibil momentan.");
        problemDetail.setTitle("Eroare de comunicare inter-servicii");
        return problemDetail;
    }

    // Intercepteaza erorile WebClient propagate de la microservicii
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ProblemDetail> handleWebClientResponseException(WebClientResponseException ex) {
        log.error("Eroare WebClient [{}] la apelul: {}", ex.getStatusCode(), ex.getRequest() != null ? ex.getRequest().getURI() : "unknown URL");
        try {
            ProblemDetail downstreamProblem = ex.getResponseBodyAs(ProblemDetail.class);
            if (downstreamProblem != null) {
                return ResponseEntity.status(ex.getStatusCode()).body(downstreamProblem);
            }
        } catch (Exception e) {
        }
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getResponseBodyAsString());
        problemDetail.setTitle("Eroare Serviciu DependentPropagată");
        return ResponseEntity.status(ex.getStatusCode()).body(problemDetail);
    }

    // specific WebFlux (in loc de MethodArgumentNotValidException)
    @ExceptionHandler(WebExchangeBindException.class)
    public ProblemDetail handleValidationExceptions(WebExchangeBindException ex) {
        log.warn("Eroare de validare a datelor de intrare. Numar erori: {}", ex.getErrorCount());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Eroare de validare a datelor de intrare.");
        problemDetail.setTitle("Validare Eșuată");

        Map<String, String> eroriCampuri = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            eroriCampuri.put(error.getField(), error.getDefaultMessage());
        }
        
        problemDetail.setProperty("erori_campuri", eroriCampuri);
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Eroare de validare a argumentelor: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Date Invalide");
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllOtherExceptions(Exception ex) {
        log.error("Eroare internă neprevăzută:", ex); 
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "A apărut o eroare internă pe server. Vă rugăm să reîncercați mai târziu.");
        problemDetail.setTitle("Eroare Internă");
        return problemDetail;
    }
}