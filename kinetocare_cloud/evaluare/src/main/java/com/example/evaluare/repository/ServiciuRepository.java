package com.example.evaluare.repository;

import com.example.common.enums.TipServiciu;
import com.example.evaluare.domain.Serviciu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiciuRepository extends JpaRepository<Serviciu, Long> {
    Optional<Serviciu> findByTipServiciu(TipServiciu tipServiciu);
}

