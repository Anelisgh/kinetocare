package com.example.kinetocare.repository;

import com.example.kinetocare.domain.Pacient;
import com.example.kinetocare.domain.Terapeut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacientRepository extends JpaRepository<Pacient, Long> {
    List<Pacient> findAll();
    List<Pacient> findByTerapeutOrderByNumeAsc(Terapeut terapeut);
    @Query("SELECT p FROM Pacient p WHERE p.user.email = :email")
    Optional<Pacient> findByUserEmail(@Param("email") String email);
}
