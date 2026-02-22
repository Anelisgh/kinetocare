package com.example.servicii_service.repository;

import com.example.servicii_service.entity.Serviciu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ServiciuRepository extends JpaRepository<Serviciu, Long> {

    @NonNull
    @Query("SELECT s FROM Serviciu s JOIN FETCH s.tipServiciu")
    List<Serviciu> findAll();

    @Query("SELECT s FROM Serviciu s JOIN FETCH s.tipServiciu WHERE LOWER(s.tipServiciu.nume) LIKE LOWER(CONCAT('%', :numeTip, '%'))")
    List<Serviciu> findByTipServiciu_NumeContainingIgnoreCase(@Param("numeTip") String numeTip);
    
    // Cautare dupa nume specific
    @Query("SELECT s FROM Serviciu s JOIN FETCH s.tipServiciu WHERE LOWER(s.nume) LIKE LOWER(CONCAT('%', :nume, '%'))")
    List<Serviciu> findByNumeContainingIgnoreCase(@Param("nume") String nume);
}
