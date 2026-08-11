package com.example.demo.controller;

import com.example.demo.dao.ApiResponseDao;
import com.example.demo.dao.ModuleDao;
import com.example.demo.service.ModuleService;

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
@RequestMapping("/admin/module")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService service;

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDao<ModuleDao>> saveModule(
            @RequestParam(required = false) UUID id,
            @RequestBody ModuleDao dao) {

        dao.setId(id);

        ApiResponseDao<ModuleDao> response = service.saveOrUpdate(dao);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDao<Page<ModuleDao>>> listModules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String activeFlag) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id"));

        ApiResponseDao<Page<ModuleDao>> response = service.listModules(pageable, activeFlag);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDao<Void>> deleteModule(
            @RequestParam UUID id) {

        ApiResponseDao<Void> response = service.deleteModule(id);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @GetMapping("/getById")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDao<ModuleDao>> getModuleById(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) String activeFlag) {

        if (id == null) {

            ApiResponseDao<ModuleDao> response = ApiResponseDao.error(
                    400,
                    "Id parameter is required",
                    "MISSING_ID");

            return ResponseEntity
                    .status(400)
                    .body(response);
        }

        ApiResponseDao<ModuleDao> response = service.getById(id, activeFlag);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }
}