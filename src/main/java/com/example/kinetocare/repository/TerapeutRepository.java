package com.example.kinetocare.repository;

import com.example.kinetocare.domain.Terapeut;
import com.example.kinetocare.domain.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TerapeutRepository extends JpaRepository<Terapeut, Long> {
    Optional<Terapeut> findByUserEmail(String email);
}
