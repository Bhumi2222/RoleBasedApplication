package com.example.demo.service;

import com.example.demo.dao.ApiResponseDao;
import com.example.demo.dao.PermissionDao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PermissionService {

    ApiResponseDao<PermissionDao> saveOrUpdate(PermissionDao dao);

    ApiResponseDao<Page<PermissionDao>> listPermissions(Pageable pageable, String activeFlag);

    ApiResponseDao<Void> deletePermission(UUID id);

    ApiResponseDao<PermissionDao> getById(UUID id, String activeFlag);
}