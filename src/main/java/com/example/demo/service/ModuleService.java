package com.example.demo.service;

import com.example.demo.dao.ApiResponseDao;
import com.example.demo.dao.ModuleDao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ModuleService {

    ApiResponseDao<ModuleDao> saveOrUpdate(ModuleDao dao);

    ApiResponseDao<Page<ModuleDao>> listModules(Pageable pageable, String activeFlag);

    ApiResponseDao<Void> deleteModule(UUID id);

    ApiResponseDao<ModuleDao> getById(UUID id, String activeFlag);
}