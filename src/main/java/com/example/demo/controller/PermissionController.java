package com.example.demo.controller;

import com.example.demo.dao.ApiResponseDao;
import com.example.demo.dao.PermissionDao;
import com.example.demo.service.PermissionService;
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
@RequestMapping("/admin/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService service;

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDao<PermissionDao>> savePermission(
            @RequestParam(required = false) UUID id,
            @RequestBody PermissionDao dao) {

        dao.setId(id);

        log.info("Save permission request - Code: {}", dao.getPermissionCode());

        ApiResponseDao<PermissionDao> response = service.saveOrUpdate(dao);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDao<Page<PermissionDao>>> listPermissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String activeFlag) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id"));

        ApiResponseDao<Page<PermissionDao>> response = service.listPermissions(pageable, activeFlag);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDao<Void>> deletePermission(
            @RequestParam UUID id) {

        log.info("Delete permission request - Id: {}", id);

        ApiResponseDao<Void> response = service.deletePermission(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/getById")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDao<PermissionDao>> getPermissionById(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) String activeFlag) {

        if (id == null) {
            ApiResponseDao<PermissionDao> response = ApiResponseDao.error(
                    400,
                    "Id parameter is required",
                    "MISSING_ID");

            return ResponseEntity.status(400).body(response);
        }

        ApiResponseDao<PermissionDao> response = service.getById(id, activeFlag);

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}