package com.example.demo.controller;

import com.example.demo.dao.LoginDao;
import com.example.demo.dao.LoginResponseDao;
import com.example.demo.dao.UserDao;
import com.example.demo.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDao> login(
            @RequestBody LoginDao loginDao) {

        LoginResponseDao response = authService.login(loginDao);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDao> getCurrentUser() {

        UserDao user = authService.getCurrentUser();

        return ResponseEntity.ok(user);
    }   
}