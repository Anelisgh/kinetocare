package com.example.programari_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;

@Entity
@Table(name = "evolutii", indexes = {
                @Index(name = "idx_evolutie_pacient", columnList = "pacient_id"),
                @Index(name = "idx_evolutie_terapeut", columnList = "terapeut_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evolutie {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "pacient_id", nullable = false)
        private Long pacientId;

        @Column(name = "terapeut_id", nullable = false)
        private Long terapeutId;

        @Column(columnDefinition = "TEXT", nullable = false)
        private String observatii;

        @CreationTimestamp
        @Column(name = "created_at", nullable = false, updatable = false)
        private OffsetDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private OffsetDateTime updatedAt;
}
