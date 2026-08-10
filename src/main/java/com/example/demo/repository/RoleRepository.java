package com.example.demo.repository;

import com.example.demo.Entity.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository
                extends JpaRepository<Role, UUID> {

        boolean existsByRoleNameIgnoreCaseAndIsActive(
                        String roleName,
                        Character isActive);

        Page<Role> findByIsActive(
                        Character isActive,
                        Pageable pageable);

        Optional<Role> findByIdAndIsActive(
                        UUID id,
                        Character isActive);
}