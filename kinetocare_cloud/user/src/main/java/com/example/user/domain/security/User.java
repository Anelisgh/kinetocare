package com.example.user.domain.security;

import com.example.user.domain.Pacient;
import com.example.user.domain.Terapeut;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder  // Adaugat pentru Builder-ul din mapper
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private RoleType roleType;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Pacient pacient;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Terapeut terapeut;

    // Flag-uri pentru security
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
}
