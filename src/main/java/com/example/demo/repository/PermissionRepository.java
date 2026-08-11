package com.example.demo.repository;

import com.example.demo.Entity.Permission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {

        boolean existsByPermissionCodeIgnoreCase(String permissionCode);

        Page<Permission> findByIsActive(Character isActive, Pageable pageable);

        List<Permission> findAllByIsActive(Character isActive);

        Optional<Permission> findByIdAndIsActive(UUID id, Character isActive);

        List<Permission> findByModule_IdAndIsActive(UUID moduleId, Character isActive);
}