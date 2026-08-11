package com.example.demo.serviceImpl;

import com.example.demo.dao.ApiResponseDao;
import com.example.demo.dao.PermissionDao;
import com.example.demo.Entity.Module;
import com.example.demo.Entity.Permission;
import com.example.demo.repository.ModuleRepository;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository repository;
    private final ModuleRepository moduleRepository;

    @Override
    public ApiResponseDao<PermissionDao> saveOrUpdate(PermissionDao dao) {
        Permission entity;

        if (dao.getId() == null) {
            if (dao.getPermissionCode() == null || dao.getPermissionCode().trim().isEmpty()) {
                return ApiResponseDao.error(400, "Permission code is required", "PERMISSION_CODE_REQUIRED");
            }

            if (dao.getPermissionName() == null || dao.getPermissionName().trim().isEmpty()) {
                return ApiResponseDao.error(400, "Permission name is required", "PERMISSION_NAME_REQUIRED");
            }

            if (dao.getModuleId() == null) {
                return ApiResponseDao.error(400, "Module ID is required", "MODULE_ID_REQUIRED");
            }

            String permissionCode = dao.getPermissionCode().trim().toUpperCase();

            if (repository.existsByPermissionCodeIgnoreCase(permissionCode)) {
                return ApiResponseDao.error(409, "Permission code already exists", "PERMISSION_DUPLICATE");
            }

            Module module = moduleRepository.findById(dao.getModuleId()).orElse(null);

            if (module == null) {
                return ApiResponseDao.error(404, "Module not found", "MODULE_NOT_FOUND");
            }

            entity = new Permission();
            entity.setPermissionCode(permissionCode);
            entity.setPermissionName(dao.getPermissionName().trim());
            entity.setModule(module);
            entity.setIsActive(dao.getIsActive() != null ? dao.getIsActive() : 'Y');

        } else {
            entity = repository.findById(dao.getId()).orElse(null);

            if (entity == null) {
                return ApiResponseDao.error(404, "Permission not found", "PERMISSION_NOT_FOUND");
            }

            if (dao.getPermissionCode() != null && !dao.getPermissionCode().trim().isEmpty()) {
                String permissionCode = dao.getPermissionCode().trim().toUpperCase();

                if (!entity.getPermissionCode().equalsIgnoreCase(permissionCode)
                        && repository.existsByPermissionCodeIgnoreCase(permissionCode)) {
                    return ApiResponseDao.error(409, "Permission code already exists", "PERMISSION_DUPLICATE");
                }

                entity.setPermissionCode(permissionCode);
            }

            if (dao.getPermissionName() != null && !dao.getPermissionName().trim().isEmpty()) {
                entity.setPermissionName(dao.getPermissionName().trim());
            }

            if (dao.getModuleId() != null) {
                Module module = moduleRepository.findById(dao.getModuleId()).orElse(null);

                if (module == null) {
                    return ApiResponseDao.error(404, "Module not found", "MODULE_NOT_FOUND");
                }

                entity.setModule(module);
            }

            if (dao.getIsActive() != null) {
                entity.setIsActive(dao.getIsActive());
            }
        }

        Permission saved = repository.save(entity);

        return ApiResponseDao.success(
                dao.getId() == null ? "Permission created successfully" : "Permission updated successfully",
                convertToDao(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDao<Page<PermissionDao>> listPermissions(Pageable pageable, String activeFlag) {
        Page<Permission> result;

        if (activeFlag != null && !activeFlag.isBlank()) {
            result = repository.findByIsActive(activeFlag.charAt(0), pageable);
        } else {
            result = repository.findAll(pageable);
        }

        Page<PermissionDao> mapped = result.map(this::convertToDao);

        return ApiResponseDao.success("Permissions fetched successfully", mapped);
    }

    @Override
    public ApiResponseDao<Void> deletePermission(UUID id) {
        if (id == null) {
            return ApiResponseDao.error(400, "Permission ID required", "ID_REQUIRED");
        }

        Permission entity = repository.findById(id).orElse(null);

        if (entity == null) {
            return ApiResponseDao.error(404, "Permission not found", "PERMISSION_NOT_FOUND");
        }

        if ('N' == entity.getIsActive()) {
            return ApiResponseDao.error(400, "Permission already inactive", "ALREADY_INACTIVE");
        }

        entity.setIsActive('N');
        repository.save(entity);

        return ApiResponseDao.success("Permission deleted successfully", null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDao<PermissionDao> getById(UUID id, String activeFlag) {
        if (id == null) {
            return ApiResponseDao.error(400, "Permission ID required", "ID_REQUIRED");
        }

        Permission entity;

        if (activeFlag != null && !activeFlag.isBlank()) {
            entity = repository.findByIdAndIsActive(id, activeFlag.charAt(0)).orElse(null);
        } else {
            entity = repository.findById(id).orElse(null);
        }

        if (entity == null) {
            return ApiResponseDao.error(404, "Permission not found", "PERMISSION_NOT_FOUND");
        }

        return ApiResponseDao.success("Permission fetched successfully", convertToDao(entity));
    }

    private PermissionDao convertToDao(Permission entity) {
        return PermissionDao.builder()
                .id(entity.getId())
                .permissionCode(entity.getPermissionCode())
                .permissionName(entity.getPermissionName())
                .moduleId(entity.getModule() != null ? entity.getModule().getId() : null)
                .isActive(entity.getIsActive())
                .build();
    }
}