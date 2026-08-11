package com.example.demo.repository;

import com.example.demo.Entity.RolePermission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    List<RolePermission> findByRoleIdAndIsActive(UUID roleId, Character isActive);

    List<RolePermission> findByRole_IdAndIsActive(
            UUID roleId,
            Character isActive);

    List<RolePermission> findByRole_IdAndPermission_Module_IdAndIsActive(
            UUID roleId,
            UUID moduleId,
            Character isActive);

    boolean existsByRole_IdAndPermission_IdAndIsActive(
            UUID roleId,
            UUID permissionId,
            Character isActive);
}