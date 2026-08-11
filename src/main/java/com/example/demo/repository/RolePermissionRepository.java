package com.example.demo.repository;

import com.example.demo.Entity.RolePermission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    List<RolePermission> findByRoleIdAndIsActive(UUID roleId, Character isActive);

    List<RolePermission> findByRoleIdAndModuleIdAndIsActive(
            UUID roleId,
            UUID moduleId,
            Character isActive);
}