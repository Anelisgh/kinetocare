package com.example.user.repository;

import com.example.user.domain.Terapeut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TerapeutRepository extends JpaRepository<Terapeut, Long> {
    Optional<Terapeut> findByUserEmail(String email);
}
