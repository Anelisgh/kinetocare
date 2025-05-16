package com.example.kinetocare.repository;

import com.example.kinetocare.domain.Diagnostic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticRepository extends JpaRepository<Diagnostic, Long> {
}
