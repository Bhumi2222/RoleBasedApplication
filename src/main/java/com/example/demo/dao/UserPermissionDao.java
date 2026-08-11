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
public class UserPermissionDao {
    private UUID permissionId;
    private String permissionCode;
    private String permissionName;
    private boolean assigned;
}