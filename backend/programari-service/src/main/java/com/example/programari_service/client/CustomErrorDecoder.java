package com.example.programari_service.client;

import com.example.programari_service.exception.ExternalServiceException;
import com.example.programari_service.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new ExternalServiceException("Eroare de validare a datelor trimise.");
            case 404 -> new ResourceNotFoundException("Resursa cerută din serviciul extern nu a fost găsită.");
            case 500 -> new ExternalServiceException("Serviciul extern a întâmpinat o eroare internă.");
            case 503 -> new ExternalServiceException("Serviciul extern este indisponibil.");
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }
}
