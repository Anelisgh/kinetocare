package com.example.terapeuti_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "locatii",
        indexes = {
                @Index(name = "idx_locatii_oras", columnList = "oras")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Locatie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nume; // ex "Kineto Bebe Mosilor"

    @Column(nullable = false, length = 300)
    private String adresa;

    @Column(nullable = false, length = 100)
    private String oras;

    @Column(nullable = false, length = 100)
    private String judet;

    @Column(name = "cod_postal", length = 10)
    private String codPostal;

    @Column(length = 20)
    private String telefon;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
