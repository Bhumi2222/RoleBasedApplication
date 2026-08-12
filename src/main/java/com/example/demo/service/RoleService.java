package com.example.demo.service;

import com.example.demo.dao.ApiResponseDao;
import com.example.demo.dao.RoleDao;
import com.example.demo.dao.RolePermissionAssignDao;
import com.example.demo.dao.RolePermissionViewDao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RoleService {

        ApiResponseDao<RoleDao> saveOrUpdate(RoleDao dao);

        ApiResponseDao<Page<RoleDao>> listRoles(
                        Pageable pageable,
                        String activeFlag);

        ApiResponseDao<Void> deleteRole(UUID id);

        ApiResponseDao<RoleDao> getById(
                        UUID id,
                        String activeFlag);

        ApiResponseDao<RoleDao> assignPermissions(RolePermissionAssignDao dao);

        ApiResponseDao<RolePermissionViewDao> getRolePermissions(UUID roleId);

        ApiResponseDao<RolePermissionViewDao> updateRolePermissions(
                        RolePermissionAssignDao dao);

}