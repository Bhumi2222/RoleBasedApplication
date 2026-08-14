package com.example.demo.service;

import com.example.demo.dao.LoginDao;
import com.example.demo.dao.LoginResponseDao;
import com.example.demo.dao.UserDao;

public interface AuthService {

    LoginResponseDao login(LoginDao loginDao);

    UserDao getCurrentUser();

}