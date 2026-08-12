package com.example.demo.repository;

import com.example.demo.Entity.RolePermission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
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

  @Query("""
          SELECT rp.permission.permissionCode
          FROM RolePermission rp
          WHERE rp.role.id = :roleId
            AND rp.isActive = 'Y'
            AND rp.permission.isActive = 'Y'
      """)
  Set<String> findPermissionCodesByRoleId(
      @Param("roleId") UUID roleId);

}