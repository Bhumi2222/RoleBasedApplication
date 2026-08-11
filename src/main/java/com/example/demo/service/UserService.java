package com.example.demo.service;

import com.example.demo.dao.ApiResponseDao;
import com.example.demo.dao.UserDao;
import com.example.demo.dao.UserDetailsDao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    ApiResponseDao<UserDao> saveOrUpdate(UserDao dao);

    ApiResponseDao<Page<UserDao>> listUsers(Pageable pageable, String activeFlag);

    ApiResponseDao<Void> deleteUser(UUID id);

    ApiResponseDao<UserDao> getById(UUID id, String activeFlag);

    ApiResponseDao<UserDetailsDao> getUserDetails(UUID userId);
}