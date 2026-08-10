package com.example.demo.repository;

import com.example.demo.Entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByUsernameIgnoreCase(String username);

    Optional<User> findByUsername(String username);

    Page<User> findByIsActive(
            Character isActive,
            Pageable pageable);

    Optional<User> findByIdAndIsActive(
            UUID id,
            Character isActive);
}