package com.example.evaluare.repository;

import com.example.evaluare.domain.Diagnostic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticRepository extends JpaRepository<Diagnostic, Long>  {
}
