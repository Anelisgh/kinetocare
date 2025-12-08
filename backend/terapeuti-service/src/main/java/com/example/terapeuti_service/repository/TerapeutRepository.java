package com.example.terapeuti_service.repository;

import com.example.terapeuti_service.entity.Specializare;
import com.example.terapeuti_service.entity.Terapeut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TerapeutRepository extends JpaRepository<Terapeut, Long> {
    Optional<Terapeut> findByKeycloakId(String keycloakId);
    boolean existsByKeycloakId(String keycloakId);
    List<Terapeut> findBySpecializareAndActiveTrue(Specializare specializare);
}
