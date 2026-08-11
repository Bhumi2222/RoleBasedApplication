package com.example.demo.service;

import com.example.demo.dao.ApiPermissionDao;
import com.example.demo.dao.ApiResponseDao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ApiPermissionService {

    ApiResponseDao<ApiPermissionDao> saveOrUpdate(
            ApiPermissionDao dao);

    ApiResponseDao<Page<ApiPermissionDao>> listApiPermissions(
            Pageable pageable,
            String activeFlag);

    ApiResponseDao<ApiPermissionDao> getById(
            UUID id,
            String activeFlag);

    ApiResponseDao<Void> deleteApiPermission(
            UUID id);
}