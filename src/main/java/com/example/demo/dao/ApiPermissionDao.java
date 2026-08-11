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
public class ApiPermissionDao {

    private UUID id;
    private String apiPath;
    private String httpMethod;
    private UUID permissionId;
    private Character isActive;
}