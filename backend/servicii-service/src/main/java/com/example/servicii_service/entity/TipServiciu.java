package com.example.servicii_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tip_serviciu", indexes = {
        @Index(name = "idx_tip_serviciu_nume", columnList = "nume")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.SuperBuilder
public class TipServiciu extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nume;

    @Column(columnDefinition = "TEXT")
    private String descriere;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
