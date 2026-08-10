package com.example.demo.serviceImpl;

import com.example.demo.dao.ApiResponseDao;
import com.example.demo.dao.UserDao;
import com.example.demo.Entity.Role;
import com.example.demo.Entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    // Create or Update
    @Override
    public ApiResponseDao<UserDao> saveOrUpdate(
            UserDao dao) {

        User entity;

        // =========================
        // CREATE
        // =========================
        if (dao.getId() == null) {

            if (dao.getUsername() == null ||
                    dao.getUsername().trim().isEmpty()) {

                return ApiResponseDao.error(
                        400,
                        "Username is required",
                        "USERNAME_REQUIRED");
            }

            if (dao.getPassword() == null ||
                    dao.getPassword().trim().isEmpty()) {

                return ApiResponseDao.error(
                        400,
                        "Password is required",
                        "PASSWORD_REQUIRED");
            }

            if (dao.getRoleId() == null) {

                return ApiResponseDao.error(
                        400,
                        "Role ID is required",
                        "ROLE_ID_REQUIRED");
            }

            String username = dao.getUsername().trim();

            // Check duplicate username
            if (repository.existsByUsernameIgnoreCase(
                    username)) {

                return ApiResponseDao.error(
                        409,
                        "Username already exists",
                        "USERNAME_DUPLICATE");
            }

            // Find role
            Role role = roleRepository
                    .findById(dao.getRoleId())
                    .orElse(null);

            if (role == null) {

                return ApiResponseDao.error(
                        404,
                        "Role not found",
                        "ROLE_NOT_FOUND");
            }

            entity = new User();

            entity.setUsername(username);

            // Encrypt password
            entity.setPassword(
                    passwordEncoder.encode(
                            dao.getPassword()));

            entity.setIsActive(
                    dao.getIsActive() != null
                            ? dao.getIsActive()
                            : 'Y');

            entity.setRole(role);
        }

        // =========================
        // UPDATE
        // =========================
        else {

            entity = repository
                    .findById(dao.getId())
                    .orElse(null);

            if (entity == null) {

                return ApiResponseDao.error(
                        404,
                        "User not found",
                        "USER_NOT_FOUND");
            }

            // Update username
            if (dao.getUsername() != null &&
                    !dao.getUsername()
                            .trim()
                            .isEmpty()) {

                String username = dao.getUsername().trim();

                if (!entity.getUsername()
                        .equalsIgnoreCase(username)
                        &&
                        repository
                                .existsByUsernameIgnoreCase(
                                        username)) {

                    return ApiResponseDao.error(
                            409,
                            "Username already exists",
                            "USERNAME_DUPLICATE");
                }

                entity.setUsername(username);
            }

            // Update password only when provided
            if (dao.getPassword() != null &&
                    !dao.getPassword()
                            .trim()
                            .isEmpty()) {

                entity.setPassword(
                        passwordEncoder.encode(
                                dao.getPassword()));
            }

            // Update active status
            if (dao.getIsActive() != null) {

                entity.setIsActive(
                        dao.getIsActive());
            }

            // Update role
            if (dao.getRoleId() != null) {

                Role role = roleRepository
                        .findById(
                                dao.getRoleId())
                        .orElse(null);

                if (role == null) {

                    return ApiResponseDao.error(
                            404,
                            "Role not found",
                            "ROLE_NOT_FOUND");
                }

                entity.setRole(role);
            }
        }

        User saved = repository.save(entity);

        return ApiResponseDao.success(
                dao.getId() == null
                        ? "User created successfully"
                        : "User updated successfully",
                convertToDao(saved));
    }

    // =========================
    // GET ALL
    // =========================
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDao<Page<UserDao>> listUsers(
            Pageable pageable,
            String activeFlag) {

        Page<User> result;

        if (activeFlag != null &&
                !activeFlag.isBlank()) {

            result = repository.findByIsActive(
                    activeFlag.charAt(0),
                    pageable);

        } else {

            result = repository.findAll(pageable);
        }

        Page<UserDao> mapped = result.map(this::convertToDao);

        return ApiResponseDao.success(
                "Users fetched successfully",
                mapped);
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public ApiResponseDao<Void> deleteUser(
            UUID id) {

        if (id == null) {

            return ApiResponseDao.error(
                    400,
                    "User ID required",
                    "ID_REQUIRED");
        }

        User entity = repository.findById(id)
                .orElse(null);

        if (entity == null) {

            return ApiResponseDao.error(
                    404,
                    "User not found",
                    "USER_NOT_FOUND");
        }

        if ('N' == entity.getIsActive()) {

            return ApiResponseDao.error(
                    400,
                    "User already inactive",
                    "ALREADY_INACTIVE");
        }

        // Soft delete
        entity.setIsActive('N');

        repository.save(entity);

        return ApiResponseDao.success(
                "User deleted successfully",
                null);
    }

    // =========================
    // GET BY ID
    // =========================
    @Override
    @Transactional(readOnly = true)
    public ApiResponseDao<UserDao> getById(
            UUID id,
            String activeFlag) {

        if (id == null) {

            return ApiResponseDao.error(
                    400,
                    "User ID required",
                    "ID_REQUIRED");
        }

        User entity;

        if (activeFlag != null &&
                !activeFlag.isBlank()) {

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
                    "User not found",
                    "USER_NOT_FOUND");
        }

        return ApiResponseDao.success(
                "User fetched successfully",
                convertToDao(entity));
    }

    // =========================
    // ENTITY TO DAO
    // =========================
    private UserDao convertToDao(
            User entity) {

        return UserDao.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .isActive(entity.getIsActive())
                .roleId(
                        entity.getRole() != null
                                ? entity.getRole().getId()
                                : null)
                .roleName(
                        entity.getRole() != null
                                ? entity.getRole().getRoleName()
                                : null)
                .build();
    }
}