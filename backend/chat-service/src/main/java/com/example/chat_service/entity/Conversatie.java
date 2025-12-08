package com.example.chat_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "conversatii",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_conversatie_pair",
                        columnNames = {"pacient_id", "terapeut_id"}
                )
        },
        indexes = {
                @Index(name = "idx_conv_pacient", columnList = "pacient_id"),
                @Index(name = "idx_conv_terapeut", columnList = "terapeut_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversatie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pacient_id", nullable = false)
    private Long pacientId;

    @Column(name = "terapeut_id", nullable = false)
    private Long terapeutId;

    @Column(name = "ultimul_mesaj_la")
    private OffsetDateTime ultimulMesajLa;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
