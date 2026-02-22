package com.example.user_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users", uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = { "email" }),
                @UniqueConstraint(name = "uk_users_keycloak_id", columnNames = { "keycloak_id" })
}, indexes = {
                @Index(name = "idx_users_role", columnList = "role"),
                @Index(name = "idx_users_keycloak_id", columnList = "keycloak_id"),
                @Index(name = "idx_users_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "keycloak_id", nullable = false, length = 36)
        private String keycloakId; // UUID din Keycloak

        @Column(nullable = false, length = 100)
        private String email;

        @Column(nullable = false, length = 100)
        private String nume;

        @Column(nullable = false, length = 100)
        private String prenume;

        @Column(nullable = false, length = 20)
        private String telefon;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 10)
        private Gen gen;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private UserRole role;

        @Column(nullable = false)
        @Builder.Default
        private Boolean active = true;

        @CreationTimestamp
        @Column(name = "created_at", nullable = false, updatable = false)
        private OffsetDateTime createdAt;
}
