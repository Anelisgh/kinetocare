package com.example.pacienti_service.repository;

import com.example.pacienti_service.entity.Pacient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacientRepository extends JpaRepository<Pacient, Long> {
    Optional<Pacient> findByKeycloakId(String keycloakId);
    boolean existsByKeycloakId(String keycloakId);
    boolean existsByCnp(String cnp);
}
