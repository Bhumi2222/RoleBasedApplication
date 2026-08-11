package com.example.demo.serviceImpl;

import com.example.demo.dao.ApiResponseDao;
import com.example.demo.dao.ModuleDao;
import com.example.demo.Entity.Module;
import com.example.demo.repository.ModuleRepository;
import com.example.demo.service.ModuleService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository repository;

    @Override
    public ApiResponseDao<ModuleDao> saveOrUpdate(ModuleDao dao) {

        Module entity;

        if (dao.getId() == null) {

            if (dao.getModuleName() == null ||
                    dao.getModuleName().trim().isEmpty()) {

                return ApiResponseDao.error(
                        400,
                        "Module name is required",
                        "MODULE_NAME_REQUIRED");
            }

            String moduleName = dao.getModuleName().trim();

            if (repository.existsByModuleNameIgnoreCase(moduleName)) {

                return ApiResponseDao.error(
                        409,
                        "Module name already exists",
                        "MODULE_DUPLICATE");
            }

            entity = new Module();
            entity.setModuleName(moduleName);
            entity.setIsActive(
                    dao.getIsActive() != null
                            ? dao.getIsActive()
                            : 'Y');

        } else {

            entity = repository.findById(dao.getId()).orElse(null);

            if (entity == null) {

                return ApiResponseDao.error(
                        404,
                        "Module not found",
                        "MODULE_NOT_FOUND");
            }

            if (dao.getModuleName() != null &&
                    !dao.getModuleName().trim().isEmpty()) {

                String moduleName = dao.getModuleName().trim();

                if (!entity.getModuleName().equalsIgnoreCase(moduleName)
                        && repository.existsByModuleNameIgnoreCase(moduleName)) {

                    return ApiResponseDao.error(
                            409,
                            "Module name already exists",
                            "MODULE_DUPLICATE");
                }

                entity.setModuleName(moduleName);
            }

            if (dao.getIsActive() != null) {
                entity.setIsActive(dao.getIsActive());
            }
        }

        Module saved = repository.save(entity);

        return ApiResponseDao.success(
                dao.getId() == null
                        ? "Module created successfully"
                        : "Module updated successfully",
                convertToDao(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDao<Page<ModuleDao>> listModules(
            Pageable pageable,
            String activeFlag) {

        Page<Module> result;

        if (activeFlag != null && !activeFlag.isBlank()) {

            result = repository.findByIsActive(
                    activeFlag.charAt(0),
                    pageable);

        } else {

            result = repository.findAll(pageable);
        }

        Page<ModuleDao> mapped = result.map(this::convertToDao);

        return ApiResponseDao.success(
                "Modules fetched successfully",
                mapped);
    }

    @Override
    public ApiResponseDao<Void> deleteModule(UUID id) {

        if (id == null) {

            return ApiResponseDao.error(
                    400,
                    "Module ID required",
                    "ID_REQUIRED");
        }

        Module entity = repository.findById(id).orElse(null);

        if (entity == null) {

            return ApiResponseDao.error(
                    404,
                    "Module not found",
                    "MODULE_NOT_FOUND");
        }

        if ('N' == entity.getIsActive()) {

            return ApiResponseDao.error(
                    400,
                    "Module already inactive",
                    "ALREADY_INACTIVE");
        }

        entity.setIsActive('N');

        repository.save(entity);

        return ApiResponseDao.success(
                "Module deleted successfully",
                null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDao<ModuleDao> getById(
            UUID id,
            String activeFlag) {

        if (id == null) {

            return ApiResponseDao.error(
                    400,
                    "Module ID required",
                    "ID_REQUIRED");
        }

        Module entity;

        if (activeFlag != null && !activeFlag.isBlank()) {

            entity = repository
                    .findByIdAndIsActive(
                            id,
                            activeFlag.charAt(0))
                    .orElse(null);

        } else {

            entity = repository
                    .findById(id)
                    .orElse(null);
        }

        if (entity == null) {

            return ApiResponseDao.error(
                    404,
                    "Module not found",
                    "MODULE_NOT_FOUND");
        }

        return ApiResponseDao.success(
                "Module fetched successfully",
                convertToDao(entity));
    }

    private ModuleDao convertToDao(Module entity) {

        return ModuleDao.builder()
                .id(entity.getId())
                .moduleName(entity.getModuleName())
                .isActive(entity.getIsActive())
                .build();
    }
}