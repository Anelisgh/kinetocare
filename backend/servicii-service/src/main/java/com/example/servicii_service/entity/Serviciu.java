package com.example.servicii_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "servicii",
        indexes = {
                @Index(name = "idx_servicii_tip", columnList = "tip_serviciu_id"),
                @Index(name = "idx_serviciu_nume", columnList = "nume")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Serviciu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tip_serviciu_id", nullable = false)
    private TipServiciu tipServiciu;

    private String nume;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal pret;

    @Column(name = "durata_minute", nullable = false)
    private Integer durataMinute;

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
