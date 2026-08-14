package com.example.demo.controller;

import com.example.demo.dao.ApiResponseDao;
import com.example.demo.dao.UserDao;
import com.example.demo.dao.UserDetailsDao;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserController {

        private final UserService service;

        // Create or Update
        @PostMapping("/save")
        public ResponseEntity<ApiResponseDao<UserDao>> saveUser(
                        @RequestParam(required = false) UUID id,
                        @RequestBody UserDao dao) {

                dao.setId(id);

                log.info(
                                "Save user request - Username: {}",
                                dao.getUsername());

                ApiResponseDao<UserDao> response = service.saveOrUpdate(dao);

                return ResponseEntity
                                .status(response.getStatus())
                                .body(response);
        }

        // Get All
        @GetMapping("/list")
        public ResponseEntity<ApiResponseDao<Page<UserDao>>> listUsers(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,
                        @RequestParam(required = false) String activeFlag) {

                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(
                                                Sort.Direction.DESC,
                                                "id"));

                log.info(
                                "Get user list request - page: {}, size: {}, activeFlag: {}",
                                page,
                                size,
                                activeFlag);

                ApiResponseDao<Page<UserDao>> response = service.listUsers(
                                pageable,
                                activeFlag);

                return ResponseEntity
                                .status(response.getStatus())
                                .body(response);
        }

        // Delete
        @PostMapping("/delete")
        public ResponseEntity<ApiResponseDao<Void>> deleteUser(
                        @RequestParam UUID id) {

                log.info(
                                "Delete user request - Id: {}",
                                id);

                ApiResponseDao<Void> response = service.deleteUser(id);

                return ResponseEntity
                                .status(response.getStatus())
                                .body(response);
        }

        // Get By ID
        @GetMapping("/getById")
        public ResponseEntity<ApiResponseDao<UserDao>> getUserById(
                        @RequestParam(required = false) UUID id,
                        @RequestParam(required = false) String activeFlag) {

                if (id == null) {

                        ApiResponseDao<UserDao> response = ApiResponseDao.error(
                                        400,
                                        "Id parameter is required",
                                        "MISSING_ID");

                        return ResponseEntity
                                        .status(400)
                                        .body(response);
                }

                ApiResponseDao<UserDao> response = service.getById(
                                id,
                                activeFlag);

                return ResponseEntity
                                .status(response.getStatus())
                                .body(response);
        }

        @GetMapping("/details")
        public ResponseEntity<ApiResponseDao<UserDetailsDao>> getUserDetails(
                        @RequestParam UUID id) {

                ApiResponseDao<UserDetailsDao> response = service.getUserDetails(id);

                return ResponseEntity
                                .status(response.getStatus())
                                .body(response);
        }
}