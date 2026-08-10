package com.example.demo.dao;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDao {

    private UUID id;

    private String roleName;

    private String description;

    private Character isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}