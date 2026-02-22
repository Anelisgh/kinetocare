package com.example.pacienti_service.service;

import com.example.pacienti_service.dto.PacientCompleteProfileRequest;
import com.example.pacienti_service.dto.PacientKeycloakDTO;
import com.example.pacienti_service.dto.PacientRequest;
import com.example.pacienti_service.dto.PacientResponse;
import com.example.pacienti_service.entity.Pacient;
import com.example.pacienti_service.exception.ResourceAlreadyExistsException;
import com.example.pacienti_service.exception.ResourceNotFoundException;
import com.example.pacienti_service.mapper.PacientMapper;
import com.example.pacienti_service.client.ProgramariClient;
import com.example.pacienti_service.repository.PacientRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PacientService {

    private final PacientRepository pacientRepository;
    private final PacientMapper pacientMapper;
    private final ProgramariClient programariClient;

    // in -> keycloakId; out -> datele pacientului
    @Transactional(readOnly = true)
    public PacientResponse getPacientByKeycloakId(String keycloakId) {
        Pacient pacient = pacientRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Pacient nu a fost găsit"));
        return pacientMapper.toResponse(pacient);
    }

    // in -> pacientId; out -> keycloakId
    @Transactional(readOnly = true)
    public PacientKeycloakDTO getKeycloakIdById(Long id) {
        Pacient pacient = pacientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pacientul nu există"));
        return new PacientKeycloakDTO(pacient.getId(), pacient.getKeycloakId());
    }

    // gaseste dupa id
    // folosit si in programari-service (PacientiClient)
    @Transactional(readOnly = true)
    public PacientKeycloakDTO getPacientById(Long id) {
        Pacient pacient = pacientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pacientul nu există"));
        return new PacientKeycloakDTO(pacient.getId(), pacient.getKeycloakId());
    }

    // completarea datelor pentru a intregi profilul (la prima logare, imediat dupa register)
    @Transactional
    public PacientResponse createPacient(String keycloakId, PacientCompleteProfileRequest request) { // DTO schimbat
        if (pacientRepository.existsByKeycloakId(keycloakId)) {
            throw new ResourceAlreadyExistsException("Profilul de pacient există deja");
        }
        if (pacientRepository.existsByCnp(request.cnp())) {
            throw new ResourceAlreadyExistsException("CNP-ul este deja înregistrat");
        }

        Pacient pacient = pacientMapper.toEntity(request, keycloakId);
        Pacient saved = pacientRepository.save(pacient);
        log.info("Patient profile created for keycloakId: {}", keycloakId);
        return pacientMapper.toResponse(saved);
    }

    // update profil pacient
    @Transactional
    public PacientResponse updatePacient(String keycloakId, PacientRequest request) {
        Pacient pacient = pacientRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Pacient nu a fost găsit"));

        pacientMapper.updateEntity(pacient, request);
        Pacient updated = pacientRepository.save(pacient);
        log.info("Patient profile updated for keycloakId: {}", keycloakId);
        return pacientMapper.toResponse(updated);
    }

    // cream un pacient nou gol (dupa register)
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

    // alegerea terapeutului
    @Transactional
    public PacientResponse chooseTerapeut(String pacientKeycloakId, String terapeutKeycloakId, Long locatieId) {
        Pacient pacient = pacientRepository.findByKeycloakId(pacientKeycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Pacient nu a fost găsit"));

        String oldTerapeutKeycloakId = pacient.getTerapeutKeycloakId();

        // setam noul terapeut
        pacient.setTerapeutKeycloakId(terapeutKeycloakId);
        pacient.setLocatiePreferataId(locatieId);
        Pacient updated = pacientRepository.save(pacient);

        log.info("Patient {} chose terapeut {} at location {}", pacientKeycloakId, terapeutKeycloakId, locatieId);

        // anulam programarile din viitor cu vechiul terapeut daca e diferit
        if (oldTerapeutKeycloakId != null && !oldTerapeutKeycloakId.equals(terapeutKeycloakId)) {
            try {
                programariClient.anuleazaProgramariCuTerapeut(pacientKeycloakId, oldTerapeutKeycloakId);
                log.info("Successfully requested cancellation of upcoming appointments for pacientKeycloakId {} and old terapeut {}", pacientKeycloakId, oldTerapeutKeycloakId);
            } catch (Exception e) {
                log.error("Failed to cancel old appointments for pacientKeycloakId {} and old terapeut {}: {}", pacientKeycloakId, oldTerapeutKeycloakId, e.getMessage());
            }
        }

        return pacientMapper.toResponse(updated);
    }

    // stergerea terapeutului
    @Transactional
    public PacientResponse removeTerapeut(String pacientKeycloakId) {
        Pacient pacient = pacientRepository.findByKeycloakId(pacientKeycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Pacient nu a fost găsit"));

        String oldTerapeutKeycloakId = pacient.getTerapeutKeycloakId();

        pacient.setTerapeutKeycloakId(null);
        Pacient updated = pacientRepository.save(pacient);

        log.info("Patient {} removed terapeut", pacientKeycloakId);

        // anulam programarile din viitor cu vechiul terapeut sters
        if (oldTerapeutKeycloakId != null) {
            try {
                programariClient.anuleazaProgramariCuTerapeut(pacientKeycloakId, oldTerapeutKeycloakId);
                log.info("Successfully requested cancellation of upcoming appointments for pacientKeycloakId {} and old terapeut {}", pacientKeycloakId, oldTerapeutKeycloakId);
            } catch (Exception e) {
                log.error("Failed to cancel old appointments for pacient {} and old terapeut {}: {}", pacientKeycloakId, oldTerapeutKeycloakId, e.getMessage());
            }
        }

        return pacientMapper.toResponse(updated);
    }

    // seteaza starea activa a pacientului (folosit de user-service la dezactivare/reactivare cont)
    @Transactional
    public void setActive(String keycloakId, boolean active) {
        Pacient pacient = pacientRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Pacient nu a fost găsit"));
        pacient.setActive(active);
        pacientRepository.save(pacient);
        log.info("Pacient {} (keycloakId={}) setat active={}", pacient.getId(), keycloakId, active);
    }
}
