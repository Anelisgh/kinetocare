package com.example.user_service.repository;

import com.example.user_service.entity.User;
import com.example.user_service.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakId(String keycloakId);
    boolean existsByEmail(String email);
    List<User> findByRole(UserRole role);
    List<User> findByActive(Boolean active);
    List<User> findByRoleAndActive(UserRole role, Boolean active);
    List<User> findByKeycloakIdIn(List<String> keycloakIds);
}