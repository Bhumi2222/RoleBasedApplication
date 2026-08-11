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
public class UserPermissionModuleDao {
    private UUID moduleId;
    private String moduleName;
    private List<UserPermissionDao> permissions;
}