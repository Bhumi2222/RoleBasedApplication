package com.example.demo.service;

import com.example.demo.dao.LoginDao;
import com.example.demo.dao.LoginResponseDao;

public interface AuthService {

    LoginResponseDao login(LoginDao loginDao);
}