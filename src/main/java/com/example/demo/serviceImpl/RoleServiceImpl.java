package com.example.demo.serviceImpl;

import com.example.demo.dao.ApiResponseDao;
import com.example.demo.dao.RoleDao;
import com.example.demo.dao.RolePermissionAssignDao;
import com.example.demo.dao.RolePermissionItemDao;
import com.example.demo.dao.RolePermissionModuleDao;
import com.example.demo.dao.RolePermissionViewDao;
import com.example.demo.Entity.Permission;
import com.example.demo.Entity.Role;
import com.example.demo.Entity.RolePermission;
import com.example.demo.repository.ModuleRepository;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RolePermissionRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.RoleService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.demo.Entity.Module;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

        private final RoleRepository repository;
        private final PermissionRepository permissionRepository;
        private final RolePermissionRepository rolePermissionRepository;
        private final ModuleRepository moduleRepository;

        // Create or Update
        @Override
        public ApiResponseDao<RoleDao> saveOrUpdate(RoleDao dao) {

                Role entity;

                // CREATE
                if (dao.getId() == null) {

                        if (dao.getRoleName() == null ||
                                        dao.getRoleName().trim().isEmpty()) {

                                return ApiResponseDao.error(
                                                400,
                                                "Role name is required",
                                                "ROLE_NAME_REQUIRED");
                        }

                        String roleName = dao.getRoleName().trim();

                        if (repository
                                        .existsByRoleNameIgnoreCaseAndIsActive(
                                                        roleName, 'Y')) {

                                return ApiResponseDao.error(
                                                409,
                                                "Role already exists",
                                                "ROLE_DUPLICATE");
                        }

                        entity = new Role();

                        entity.setRoleName(roleName);

                        entity.setDescription(
                                        dao.getDescription());

                        entity.setIsActive(
                                        dao.getIsActive() != null
                                                        ? dao.getIsActive()
                                                        : 'Y');
                }

                // UPDATE
                else {

                        entity = repository
                                        .findById(dao.getId())
                                        .orElse(null);

                        if (entity == null) {

                                return ApiResponseDao.error(
                                                404,
                                                "Role not found",
                                                "ROLE_NOT_FOUND");
                        }

                        if (dao.getRoleName() != null &&
                                        !dao.getRoleName()
                                                        .trim()
                                                        .isEmpty()) {

                                String roleName = dao.getRoleName().trim();

                                if (!entity.getRoleName()
                                                .equalsIgnoreCase(roleName)
                                                &&
                                                repository
                                                                .existsByRoleNameIgnoreCaseAndIsActive(
                                                                                roleName, 'Y')) {

                                        return ApiResponseDao.error(
                                                        409,
                                                        "Role already exists",
                                                        "ROLE_DUPLICATE");
                                }

                                entity.setRoleName(roleName);
                        }

                        if (dao.getDescription() != null) {

                                entity.setDescription(
                                                dao.getDescription());
                        }

                        if (dao.getIsActive() != null) {

                                entity.setIsActive(
                                                dao.getIsActive());
                        }
                }

                Role saved = repository.save(entity);

                return ApiResponseDao.success(
                                dao.getId() == null
                                                ? "Role created successfully"
                                                : "Role updated successfully",
                                convertToDao(saved));
        }

        // Get All
        @Override
        @Transactional(readOnly = true)
        public ApiResponseDao<Page<RoleDao>> listRoles(
                        Pageable pageable,
                        String activeFlag) {

                Page<Role> result;

                if (activeFlag != null &&
                                !activeFlag.isBlank()) {

                        result = repository
                                        .findByIsActive(
                                                        activeFlag.charAt(0),
                                                        pageable);

                } else {

                        result = repository.findAll(pageable);
                }

                Page<RoleDao> mapped = result.map(
                                this::convertToDao);

                return ApiResponseDao.success(
                                "Roles fetched successfully",
                                mapped);
        }

        // Delete
        @Override
        public ApiResponseDao<Void> deleteRole(
                        UUID id) {

                if (id == null) {

                        return ApiResponseDao.error(
                                        400,
                                        "Role ID required",
                                        "ID_REQUIRED");
                }

                Role entity = repository.findById(id)
                                .orElse(null);

                if (entity == null) {

                        return ApiResponseDao.error(
                                        404,
                                        "Role not found",
                                        "ROLE_NOT_FOUND");
                }

                if ('N' == entity.getIsActive()) {

                        return ApiResponseDao.error(
                                        400,
                                        "Role already inactive",
                                        "ALREADY_INACTIVE");
                }

                // Soft delete
                entity.setIsActive('N');

                repository.save(entity);

                return ApiResponseDao.success(
                                "Role deleted successfully",
                                null);
        }

        // Get By ID
        @Override
        @Transactional(readOnly = true)
        public ApiResponseDao<RoleDao> getById(
                        UUID id,
                        String activeFlag) {

                if (id == null) {

                        return ApiResponseDao.error(
                                        400,
                                        "Role ID required",
                                        "ID_REQUIRED");
                }

                Optional<Role> result;

                if (activeFlag != null &&
                                !activeFlag.isBlank()) {

                        result = repository
                                        .findByIdAndIsActive(
                                                        id,
                                                        activeFlag.charAt(0));

                } else {

                        result = repository.findById(id);
                }

                if (result.isEmpty()) {

                        return ApiResponseDao.error(
                                        404,
                                        "Role not found: " + id,
                                        "ROLE_NOT_FOUND");
                }

                return ApiResponseDao.success(
                                "Role fetched successfully",
                                convertToDao(result.get()));
        }

        // Entity → DAO
        private RoleDao convertToDao(
                        Role entity) {

                return RoleDao.builder()
                                .id(entity.getId())
                                .roleName(
                                                entity.getRoleName())
                                .description(
                                                entity.getDescription())
                                .isActive(
                                                entity.getIsActive())
                                .build();
        }

        @Override
        public ApiResponseDao<RoleDao> assignPermissions(RolePermissionAssignDao dao) {
                if (dao.getRoleId() == null) {
                        return ApiResponseDao.error(400, "Role ID is required", "ROLE_ID_REQUIRED");
                }
                Role role = repository.findById(dao.getRoleId()).orElse(null);
                if (role == null) {
                        return ApiResponseDao.error(404, "Role not found", "ROLE_NOT_FOUND");
                }
                if ('N' == role.getIsActive()) {
                        return ApiResponseDao.error(400, "Role is inactive", "ROLE_INACTIVE");
                }
                Set<Permission> permissions = new HashSet<>();
                if ("ADMIN".equalsIgnoreCase(role.getRoleName())) {
                        permissions.addAll(
                                        permissionRepository.findAllByIsActive('Y'));
                } else {
                        if (dao.getPermissionIds() == null || dao.getPermissionIds().isEmpty()) {
                                return ApiResponseDao.error(400, "Permission IDs are required",
                                                "PERMISSION_IDS_REQUIRED");
                        }
                        for (UUID permissionId : dao.getPermissionIds()) {
                                Permission permission = permissionRepository.findById(permissionId).orElse(null);
                                if (permission == null) {
                                        return ApiResponseDao.error(404, "Permission not found",
                                                        "PERMISSION_NOT_FOUND");
                                }
                                if ('N' == permission.getIsActive()) {
                                        return ApiResponseDao.error(400, "Permission is inactive",
                                                        "PERMISSION_INACTIVE");
                                }
                                permissions.add(permission);
                        }
                }
                Set<RolePermission> rolePermissions = new HashSet<>();

                for (Permission permission : permissions) {
                        RolePermission rolePermission = RolePermission.builder()
                                        .role(role)

                                        .permission(permission)
                                        .isActive('Y')
                                        .build();

                        rolePermissions.add(rolePermission);
                }

                role.getRolePermissions().clear();
                role.getRolePermissions().addAll(rolePermissions);

                Role saved = repository.save(role);

                return ApiResponseDao.success(
                                "Permissions assigned successfully",
                                convertToDao(saved));
        }

        @Override
        @Transactional(readOnly = true)
        public ApiResponseDao<RolePermissionViewDao> getRolePermissions(UUID roleId) {

                Role role = null;

                if (roleId != null) {
                        role = repository.findById(roleId).orElse(null);

                        if (role == null) {
                                return ApiResponseDao.error(
                                                404,
                                                "Role not found",
                                                "ROLE_NOT_FOUND");
                        }
                }

                final Set<UUID> assignedPermissionIds;

                if (roleId != null) {
                        List<RolePermission> assignedPermissions = rolePermissionRepository.findByRoleIdAndIsActive(
                                        roleId, 'Y');

                        assignedPermissionIds = assignedPermissions.stream()
                                        .map(rolePermission -> rolePermission.getPermission().getId())
                                        .collect(Collectors.toSet());
                } else {
                        assignedPermissionIds = new HashSet<>();
                }

                List<Module> modules = moduleRepository.findByIsActive('Y');

                List<RolePermissionModuleDao> moduleList = new ArrayList<>();

                for (Module module : modules) {

                        List<Permission> permissions = permissionRepository.findByModule_IdAndIsActive(
                                        module.getId(),
                                        'Y');

                        List<RolePermissionItemDao> permissionList = permissions.stream()
                                        .map(permission -> RolePermissionItemDao.builder()
                                                        .permissionId(permission.getId())
                                                        .permissionCode(permission.getPermissionCode())
                                                        .permissionName(permission.getPermissionName())
                                                        .assigned(
                                                                        assignedPermissionIds.contains(
                                                                                        permission.getId()))
                                                        .build())
                                        .collect(Collectors.toList());

                        moduleList.add(
                                        RolePermissionModuleDao.builder()
                                                        .moduleId(module.getId())
                                                        .moduleName(module.getModuleName())
                                                        .permissions(permissionList)
                                                        .build());
                }

                RolePermissionViewDao response = RolePermissionViewDao.builder()
                                .roleId(role != null ? role.getId() : null)
                                .roleName(role != null ? role.getRoleName() : null)
                                .modules(moduleList)
                                .build();

                return ApiResponseDao.success(
                                "Modules and permissions fetched successfully",
                                response);
        }

        @Override
        @Transactional
        public ApiResponseDao<RolePermissionViewDao> updateRolePermissions(
                        RolePermissionAssignDao dao) {

                if (dao.getRoleId() == null) {
                        return ApiResponseDao.error(
                                        400,
                                        "Role ID is required",
                                        "ROLE_ID_REQUIRED");
                }

                Role role = repository.findById(dao.getRoleId()).orElse(null);

                if (role == null) {
                        return ApiResponseDao.error(
                                        404,
                                        "Role not found",
                                        "ROLE_NOT_FOUND");
                }

                if ('N' == role.getIsActive()) {
                        return ApiResponseDao.error(
                                        400,
                                        "Role is inactive",
                                        "ROLE_INACTIVE");
                }

                /*
                 * Get ALL role-permission records.
                 *
                 * Important:
                 * Do NOT filter by isActive = 'Y' here.
                 *
                 * We need inactive records too, because they may need
                 * to be activated again.
                 */
                List<RolePermission> existing = rolePermissionRepository.findByRole_Id(
                                dao.getRoleId());

                /*
                 * Convert selected permission IDs into a Set.
                 *
                 * This makes contains() fast and also removes duplicate IDs
                 * from the request.
                 */
                Set<UUID> selectedPermissionIds = dao.getPermissionIds() == null
                                ? Collections.emptySet()
                                : new HashSet<>(dao.getPermissionIds());

                /*
                 * Map existing permissions by permission ID.
                 *
                 * Example:
                 *
                 * ROLE_VIEW -> existing RolePermission
                 * ROLE_DELETE -> existing RolePermission
                 */
                Map<UUID, RolePermission> existingMap = existing.stream()
                                .collect(Collectors.toMap(
                                                rp -> rp.getPermission().getId(),
                                                rp -> rp));

                /*
                 * Process every selected permission.
                 */
                for (UUID permissionId : selectedPermissionIds) {

                        Permission permission = permissionRepository
                                        .findById(permissionId)
                                        .orElse(null);

                        if (permission == null) {
                                return ApiResponseDao.error(
                                                404,
                                                "Permission not found: " + permissionId,
                                                "PERMISSION_NOT_FOUND");
                        }

                        if ('N' == permission.getIsActive()) {
                                return ApiResponseDao.error(
                                                400,
                                                "Permission is inactive: " + permissionId,
                                                "PERMISSION_INACTIVE");
                        }

                        /*
                         * Check whether this role-permission combination
                         * already exists.
                         */
                        RolePermission rolePermission = existingMap.get(permissionId);

                        if (rolePermission != null) {

                                /*
                                 * Already exists.
                                 *
                                 * Just reactivate it.
                                 */
                                rolePermission.setIsActive('Y');

                        } else {

                                /*
                                 * Doesn't exist at all.
                                 *
                                 * Create a new record.
                                 */
                                rolePermission = RolePermission.builder()
                                                .role(role)
                                                .permission(permission)
                                                .isActive('Y')
                                                .build();

                                existing.add(rolePermission);
                        }
                }

                /*
                 * Any existing permission that was NOT selected
                 * should become inactive.
                 */
                for (RolePermission rolePermission : existing) {

                        UUID permissionId = rolePermission.getPermission().getId();

                        if (!selectedPermissionIds.contains(permissionId)) {
                                rolePermission.setIsActive('N');
                        }
                }

                /*
                 * Save everything together.
                 */
                rolePermissionRepository.saveAll(existing);

                return getRolePermissions(dao.getRoleId());
        }
}