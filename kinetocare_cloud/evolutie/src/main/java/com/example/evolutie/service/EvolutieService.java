package com.example.evolutie.service;

import com.example.common.dto.EvolutieDTO;
import com.example.common.dto.PacientDTO;
import com.example.common.dto.TerapeutDTO;
import com.example.evolutie.domain.Evolutie;
import com.example.evolutie.feign.PacientFeignClient;
import com.example.evolutie.feign.TerapeutFeignClient;
import com.example.evolutie.mapper.EvolutieMapper;
import com.example.evolutie.repository.EvolutieRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class EvolutieService {
    private final EvolutieRepository evolutieRepository;
    private final EvolutieMapper evolutieMapper;
    private final PacientFeignClient pacientClient;
    private final TerapeutFeignClient terapeutClient;

    @Transactional
    public void adaugaEvolutie(@Valid EvolutieDTO evolutieDTO, String emailTerapeut) {
        TerapeutDTO terapeut = getTerapeutSafely(emailTerapeut);
        if (terapeut == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Nu am putut prelua terapeutul");
        }
        PacientDTO pacient = getPacientByIdSafely(evolutieDTO.getPacientId());
        if (pacient == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Nu am putut prelua pacientul");
        }
        Evolutie evolutie = evolutieMapper.toEvolutie(evolutieDTO, pacient, terapeut);
        evolutieRepository.save(evolutie);
    }

    public List<EvolutieDTO> getEvolutiiByTerapeutId(Long terapeutId) {
        List<Evolutie> evolutii = evolutieRepository.findByTerapeutId(terapeutId);
        return evolutii.stream()
                .map(evolutieMapper::toDto)
                .collect(Collectors.toList());
    }

    @CircuitBreaker(name = "getTerapeutCB", fallbackMethod = "getTerapeutFallback")
    @Retry(name = "getTerapeutRetry")
    public TerapeutDTO getTerapeutSafely(String email) {
        return terapeutClient.getTerapeutByEmail(email);
    }

    public TerapeutDTO getTerapeutFallback(String email, Throwable t) {
        log.error("Fallback invoked for getTerapeutSafely, email: {}, error: {}", email, t.getMessage());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "User service unavailable");
    }

    @CircuitBreaker(name = "getPacientByIdCB", fallbackMethod = "getPacientByIdFallback")
    @Retry(name = "getPacientByIdRetry")
    public PacientDTO getPacientByIdSafely(Long pacientId) {
        return pacientClient.getPacientById(pacientId);
    }

    public PacientDTO getPacientByIdFallback(Long pacientId, Throwable t) {
        log.error("Fallback for pacient id: {}", pacientId, t);
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "User service unavailable");
    }
}
