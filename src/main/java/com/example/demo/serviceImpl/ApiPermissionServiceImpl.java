package com.example.demo.serviceImpl;

import com.example.demo.dao.ApiPermissionDao;
import com.example.demo.dao.ApiResponseDao;
import com.example.demo.Entity.ApiPermission;
import com.example.demo.Entity.Permission;
import com.example.demo.repository.ApiPermissionRepository;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.service.ApiPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ApiPermissionServiceImpl implements ApiPermissionService {

    private final ApiPermissionRepository repository;
    private final PermissionRepository permissionRepository;

    @Override
    public ApiResponseDao<ApiPermissionDao> saveOrUpdate(ApiPermissionDao dao) {
        ApiPermission entity;

        if (dao.getId() == null) {
            if (dao.getApiPath() == null || dao.getApiPath().trim().isEmpty()) {
                return ApiResponseDao.error(400, "API path is required", "API_PATH_REQUIRED");
            }
            if (dao.getHttpMethod() == null || dao.getHttpMethod().trim().isEmpty()) {
                return ApiResponseDao.error(400, "HTTP method is required", "HTTP_METHOD_REQUIRED");
            }
            if (dao.getPermissionId() == null) {
                return ApiResponseDao.error(400, "Permission ID is required", "PERMISSION_ID_REQUIRED");
            }

            String apiPath = dao.getApiPath().trim();
            String httpMethod = dao.getHttpMethod().trim().toUpperCase();

            if (repository.existsByApiPathAndHttpMethod(apiPath, httpMethod)) {
                return ApiResponseDao.error(409, "API permission mapping already exists", "API_PERMISSION_DUPLICATE");
            }

            Permission permission = permissionRepository.findById(dao.getPermissionId()).orElse(null);

            if (permission == null) {
                return ApiResponseDao.error(404, "Permission not found", "PERMISSION_NOT_FOUND");
            }

            if ('N' == permission.getIsActive()) {
                return ApiResponseDao.error(400, "Permission is inactive", "PERMISSION_INACTIVE");
            }

            entity = new ApiPermission();
            entity.setApiPath(apiPath);
            entity.setHttpMethod(httpMethod);
            entity.setPermission(permission);
            entity.setIsActive(dao.getIsActive() != null ? dao.getIsActive() : 'Y');

        } else {
            entity = repository.findById(dao.getId()).orElse(null);

            if (entity == null) {
                return ApiResponseDao.error(404, "API permission mapping not found", "API_PERMISSION_NOT_FOUND");
            }

            if (dao.getApiPath() != null && !dao.getApiPath().trim().isEmpty()) {
                entity.setApiPath(dao.getApiPath().trim());
            }

            if (dao.getHttpMethod() != null && !dao.getHttpMethod().trim().isEmpty()) {
                entity.setHttpMethod(dao.getHttpMethod().trim().toUpperCase());
            }

            if (dao.getPermissionId() != null) {
                Permission permission = permissionRepository.findById(dao.getPermissionId()).orElse(null);

                if (permission == null) {
                    return ApiResponseDao.error(404, "Permission not found", "PERMISSION_NOT_FOUND");
                }

                if ('N' == permission.getIsActive()) {
                    return ApiResponseDao.error(400, "Permission is inactive", "PERMISSION_INACTIVE");
                }

                entity.setPermission(permission);
            }

            if (dao.getIsActive() != null) {
                entity.setIsActive(dao.getIsActive());
            }
        }

        ApiPermission saved = repository.save(entity);

        return ApiResponseDao.success(
                dao.getId() == null ? "API permission created successfully" : "API permission updated successfully",
                convertToDao(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDao<Page<ApiPermissionDao>> listApiPermissions(Pageable pageable, String activeFlag) {
        Page<ApiPermission> result;

        if (activeFlag != null && !activeFlag.isBlank()) {
            result = repository.findByIsActive(activeFlag.charAt(0), pageable);
        } else {
            result = repository.findAll(pageable);
        }

        Page<ApiPermissionDao> mapped = result.map(this::convertToDao);

        return ApiResponseDao.success("API permissions fetched successfully", mapped);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDao<ApiPermissionDao> getById(UUID id, String activeFlag) {
        if (id == null) {
            return ApiResponseDao.error(400, "API permission ID required", "ID_REQUIRED");
        }

        ApiPermission entity;

        if (activeFlag != null && !activeFlag.isBlank()) {
            entity = repository.findById(id)
                    .filter(e -> e.getIsActive().equals(activeFlag.charAt(0)))
                    .orElse(null);
        } else {
            entity = repository.findById(id).orElse(null);
        }

        if (entity == null) {
            return ApiResponseDao.error(404, "API permission mapping not found", "API_PERMISSION_NOT_FOUND");
        }

        return ApiResponseDao.success("API permission fetched successfully", convertToDao(entity));
    }

    @Override
    public ApiResponseDao<Void> deleteApiPermission(UUID id) {
        if (id == null) {
            return ApiResponseDao.error(400, "API permission ID required", "ID_REQUIRED");
        }

        ApiPermission entity = repository.findById(id).orElse(null);

        if (entity == null) {
            return ApiResponseDao.error(404, "API permission mapping not found", "API_PERMISSION_NOT_FOUND");
        }

        if ('N' == entity.getIsActive()) {
            return ApiResponseDao.error(400, "API permission already inactive", "ALREADY_INACTIVE");
        }

        entity.setIsActive('N');
        repository.save(entity);

        return ApiResponseDao.success("API permission deleted successfully", null);
    }

    private ApiPermissionDao convertToDao(ApiPermission entity) {
        return ApiPermissionDao.builder()
                .id(entity.getId())
                .apiPath(entity.getApiPath())
                .httpMethod(entity.getHttpMethod())
                .permissionId(entity.getPermission() != null ? entity.getPermission().getId() : null)
                .isActive(entity.getIsActive())
                .build();
    }
}