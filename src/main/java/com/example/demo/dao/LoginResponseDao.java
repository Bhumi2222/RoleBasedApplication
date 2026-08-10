package com.example.demo.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDao {

    private String token;
    private String username;
    private String role;
}