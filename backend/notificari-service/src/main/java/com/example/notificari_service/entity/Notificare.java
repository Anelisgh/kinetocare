package com.example.notificari_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notificari",
        indexes = {
                @Index(name = "idx_notif_user_kc", columnList = "user_keycloak_id"),
                @Index(name = "idx_notif_citita", columnList = "este_citita"),
                @Index(name = "idx_notif_creata", columnList = "created_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_keycloak_id", nullable = false, length = 36)
    private String userKeycloakId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tip_user", nullable = false, length = 20)
    private TipUser tipUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipNotificare tip;

    @Column(nullable = false, length = 500)
    private String titlu;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String mesaj;

    @Column(name = "entitate_legata_id")
    private Long entitateLegataId; // Ex: programare_id, mesaj_id

    @Column(name = "tip_entitate_legata", length = 50)
    private String tipEntitateLegata; // Ex: "PROGRAMARE", "MESAJ"

    @Column(name = "url_actiune", length = 500)
    private String urlActiune; // URL redirect când se apasa notificarea

    @Column(name = "este_citita", nullable = false)
    @Builder.Default
    private Boolean esteCitita = false;

    @Column(name = "citita_la")
    private OffsetDateTime cititaLa;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
