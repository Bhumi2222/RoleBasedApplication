package com.example.demo.dao;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDao {

    private UUID id;

    private String username;

    private String password;

    private Character isActive;

    private UUID roleId;

    private String roleName;
}