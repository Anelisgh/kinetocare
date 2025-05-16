package com.example.kinetocare.repository;

import com.example.kinetocare.domain.Serviciu;
import com.example.kinetocare.domain.TipServiciu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiciuRepository extends JpaRepository<Serviciu, Long> {

    Optional<Serviciu> findByTipServiciu(TipServiciu tipServiciu);
}
