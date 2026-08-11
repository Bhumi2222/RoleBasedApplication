package com.example.demo.controller;

import com.example.demo.dao.ApiResponseDao;
import com.example.demo.dao.RoleDao;
import com.example.demo.dao.RolePermissionAssignDao;
import com.example.demo.dao.RolePermissionViewDao;
import com.example.demo.service.RoleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/role")
@RequiredArgsConstructor
public class RoleController {

        private final RoleService service;

        // Create or Update
        @PostMapping("/save")
        public ResponseEntity<ApiResponseDao<RoleDao>> saveRole(
                        @RequestParam(required = false) UUID id,
                        @RequestBody RoleDao dao) {

                dao.setId(id);

                log.info("Save role request - Role Name: {}",
                                dao.getRoleName());

                ApiResponseDao<RoleDao> response = service.saveOrUpdate(dao);

                return ResponseEntity
                                .status(response.getStatus())
                                .body(response);
        }

        // Get All
        @GetMapping("/list")
        public ResponseEntity<ApiResponseDao<Page<RoleDao>>> listRoles(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,
                        @RequestParam(required = false) String activeFlag) {

                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(
                                                Sort.Direction.DESC,
                                                "createdAt"));

                log.info(
                                "Get role list request - page: {}, size: {}, activeFlag: {}",
                                page,
                                size,
                                activeFlag);

                ApiResponseDao<Page<RoleDao>> response = service.listRoles(
                                pageable,
                                activeFlag);

                return ResponseEntity
                                .status(response.getStatus())
                                .body(response);
        }

        // Delete
        @PostMapping("/delete")
        public ResponseEntity<ApiResponseDao<Void>> deleteRole(
                        @RequestParam UUID id) {

                log.info("Delete role request - Id: {}", id);

                ApiResponseDao<Void> response = service.deleteRole(id);

                return ResponseEntity
                                .status(response.getStatus())
                                .body(response);
        }

        // Get By Id
        @GetMapping("/getById")
        public ResponseEntity<ApiResponseDao<RoleDao>> getRoleById(
                        @RequestParam(required = false) UUID id,
                        @RequestParam(required = false) String activeFlag) {

                if (id == null) {

                        ApiResponseDao<RoleDao> response = ApiResponseDao.error(
                                        400,
                                        "Id parameter is required",
                                        "MISSING_ID");

                        return ResponseEntity
                                        .status(400)
                                        .body(response);
                }

                log.info("Get role by id request - Id: {}", id);

                ApiResponseDao<RoleDao> response = service.getById(
                                id,
                                activeFlag);

                return ResponseEntity
                                .status(response.getStatus())
                                .body(response);
        }

        @GetMapping("/permissions")
        public ResponseEntity<ApiResponseDao<RolePermissionViewDao>> getRolePermissions(
                        @RequestParam(required = false) UUID roleId) {

                ApiResponseDao<RolePermissionViewDao> response = service.getRolePermissions(roleId);

                return ResponseEntity
                                .status(response.getStatus())
                                .body(response);
        }

        @PostMapping("/permissions/update")
        public ResponseEntity<ApiResponseDao<RolePermissionViewDao>> updateRolePermissions(
                        @RequestBody RolePermissionAssignDao dao) {

                ApiResponseDao<RolePermissionViewDao> response = service.updateRolePermissions(dao);

                return ResponseEntity
                                .status(response.getStatus())
                                .body(response);
        }
}