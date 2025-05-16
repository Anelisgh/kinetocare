package com.example.kinetocare.repository;

import com.example.kinetocare.domain.security.Authority;
import com.example.kinetocare.domain.security.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<Authority> findByRoleType(RoleType roleType);
}
