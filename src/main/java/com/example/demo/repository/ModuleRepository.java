package com.example.demo.repository;

import com.example.demo.Entity.Module;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ModuleRepository extends JpaRepository<Module, UUID> {

    boolean existsByModuleNameIgnoreCase(String moduleName);

    Page<Module> findByIsActive(Character isActive, Pageable pageable);

    Optional<Module> findByIdAndIsActive(UUID id, Character isActive);
}
