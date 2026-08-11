package com.example.demo.dao;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDao {
    private UUID id;
    private String username;
    private Character isActive;
    private UUID roleId;
    private String roleName;
    private List<UserPermissionModuleDao> modules;
}