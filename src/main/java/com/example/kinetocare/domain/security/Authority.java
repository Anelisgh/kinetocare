package com.example.kinetocare.domain.security;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter @Setter
@Table(name = "authorities", uniqueConstraints = @UniqueConstraint(columnNames = {"role_type"}))
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", unique = true)
    private RoleType roleType;

    @ManyToMany(mappedBy = "authorities", fetch = FetchType.LAZY)
    @ToString.Exclude // evitam recursivitatea infinita in relatia cu User
    @EqualsAndHashCode.Exclude // evitam recursivitatea infinita in relatia cu User
    private Set<User> users = new HashSet<>();
}
// Am inserat valorile in sql, astfel nu ar fi functionat inregistrarea
//mysql> INSERT INTO authorities (role_type) VALUES ('ROLE_PACIENT');
//mysql> INSERT INTO authorities (role_type) VALUES ('ROLE_TERAPEUT');