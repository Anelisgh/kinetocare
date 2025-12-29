package com.example.pacienti_service.service;

import com.example.pacienti_service.dto.PacientCompleteProfileRequest;
import com.example.pacienti_service.dto.PacientKeycloakDTO;
import com.example.pacienti_service.dto.PacientRequest;
import com.example.pacienti_service.dto.PacientResponse;
import com.example.pacienti_service.entity.Pacient;
import com.example.pacienti_service.mapper.PacientMapper;
import com.example.pacienti_service.repository.PacientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PacientService {

    private final PacientRepository pacientRepository;
    private final PacientMapper pacientMapper;

    public PacientResponse getPacientByKeycloakId(String keycloakId) {
        Pacient pacient = pacientRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Pacient nu a fost găsit"));
        return pacientMapper.toResponse(pacient);
    }

    // in -> pacientId; out -> keycloakId
    public PacientKeycloakDTO getKeycloakIdById(Long id) {
        Pacient pacient = pacientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pacientul nu există"));

        return new PacientKeycloakDTO(pacient.getId(), pacient.getKeycloakId());
    }

    // Called by programari-service via Feign client
    public PacientKeycloakDTO getPacientById(Long id) {
        Pacient pacient = pacientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pacientul nu există"));
        return new PacientKeycloakDTO(pacient.getId(), pacient.getKeycloakId());
    }

    @Transactional
    public PacientResponse createPacient(String keycloakId, PacientCompleteProfileRequest request) { // DTO schimbat
        if (pacientRepository.existsByKeycloakId(keycloakId)) {
            throw new RuntimeException("Profilul de pacient există deja");
        }
        if (pacientRepository.existsByCnp(request.getCnp())) {
            throw new RuntimeException("CNP-ul este deja înregistrat");
        }

        Pacient pacient = pacientMapper.toEntity(request, keycloakId);
        Pacient saved = pacientRepository.save(pacient);
        log.info("Patient profile created for keycloakId: {}", keycloakId);
        return pacientMapper.toResponse(saved);
    }

    @Transactional
    public PacientResponse updatePacient(String keycloakId, PacientRequest request) {
        Pacient pacient = pacientRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Pacient nu a fost găsit"));

        pacientMapper.updateEntity(pacient, request);
        Pacient updated = pacientRepository.save(pacient);
        log.info("Patient profile updated for keycloakId: {}", keycloakId);
        return pacientMapper.toResponse(updated);
    }

    @Transactional
    public void initializeEmptyPacient(String keycloakId) {
        // verificam daca profil exista deja
        if (pacientRepository.existsByKeycloakId(keycloakId)) {
            log.debug("Patient profile already exists for: {}", keycloakId);
            return;
        }

        Pacient pacient = Pacient.builder()
                .keycloakId(keycloakId)
                .build();

        pacientRepository.save(pacient);
        log.info("Empty patient profile initialized for: {}", keycloakId);
    }

    @Transactional
    public PacientResponse chooseTerapeut(String pacientKeycloakId, String terapeutKeycloakId, Long locatieId) {
        Pacient pacient = pacientRepository.findByKeycloakId(pacientKeycloakId)
                .orElseThrow(() -> new RuntimeException("Pacient nu a fost găsit"));
        pacient.setTerapeutKeycloakId(terapeutKeycloakId);
        pacient.setLocatiePreferataId(locatieId);
        Pacient updated = pacientRepository.save(pacient);

        log.info("Patient {} chose terapeut {}", pacientKeycloakId, terapeutKeycloakId, locatieId);
        return pacientMapper.toResponse(updated);
    }

    @Transactional
    public PacientResponse removeTerapeut(String pacientKeycloakId) {
        Pacient pacient = pacientRepository.findByKeycloakId(pacientKeycloakId)
                .orElseThrow(() -> new RuntimeException("Pacient nu a fost găsit"));

        pacient.setTerapeutKeycloakId(null);
        Pacient updated = pacientRepository.save(pacient);

        log.info("Patient {} removed terapeut", pacientKeycloakId);
        return pacientMapper.toResponse(updated);
    }
}
