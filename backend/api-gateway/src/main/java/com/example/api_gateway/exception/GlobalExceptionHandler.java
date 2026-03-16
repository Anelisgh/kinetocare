package com.example.api_gateway.exception;

import com.example.common.exception.BaseGlobalExceptionHandler;
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
public class GlobalExceptionHandler extends BaseGlobalExceptionHandler {

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
            log.debug("Nu s-a putut deserializa răspunsul ca ProblemDetail, se folosește răspunsul brut.", e);
        }
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getResponseBodyAsString());
        problemDetail.setTitle("Eroare Serviciu Dependent Propagată");
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
}
