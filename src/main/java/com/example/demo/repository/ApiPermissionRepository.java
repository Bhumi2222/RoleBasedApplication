package com.example.demo.repository;

import com.example.demo.Entity.ApiPermission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApiPermissionRepository
        extends JpaRepository<ApiPermission, UUID> {

    boolean existsByApiPathAndHttpMethod(
            String apiPath,
            String httpMethod);

    Optional<ApiPermission> findByApiPathAndHttpMethodAndIsActive(
            String apiPath,
            String httpMethod,
            Character isActive);

    Page<ApiPermission> findByIsActive(
            Character isActive,
            Pageable pageable);
}