package com.example.chat_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "mesaje",
        indexes = {
                @Index(name = "idx_mesaj_conversatie", columnList = "conversatie_id"),
                @Index(name = "idx_mesaj_expeditor", columnList = "expeditor_keycloak_id"),
                @Index(name = "idx_mesaj_trimis_la", columnList = "trimis_la")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mesaj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversatie_id", nullable = false)
    private Long conversatieId;

    @Column(name = "expeditor_keycloak_id", nullable = false, length = 36)
    private String expeditorKeycloakId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tip_expeditor", nullable = false, length = 20)
    private TipExpeditor tipExpeditor;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String continut;

    @Column(name = "este_citit", nullable = false)
    @Builder.Default
    private Boolean esteCitit = false;

    @Column(name = "citit_la")
    private OffsetDateTime cititLa;

    @CreationTimestamp
    @Column(name = "trimis_la", nullable = false, updatable = false)
    private OffsetDateTime trimisLa;
}
