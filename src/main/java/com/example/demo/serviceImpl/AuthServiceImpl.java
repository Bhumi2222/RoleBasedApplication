package com.example.demo.serviceImpl;

import com.example.demo.dao.LoginDao;
import com.example.demo.dao.LoginResponseDao;
import com.example.demo.Entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.AuthService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl
                implements AuthService {

        private final AuthenticationManager authenticationManager;

        private final UserRepository userRepository;

        private final JwtService jwtService;

        public AuthServiceImpl(
                        AuthenticationManager authenticationManager,
                        UserRepository userRepository,
                        JwtService jwtService) {

                this.authenticationManager = authenticationManager;

                this.userRepository = userRepository;

                this.jwtService = jwtService;
        }

        @Override
        public LoginResponseDao login(
                        LoginDao loginDao) {

                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                loginDao.getUsername(),
                                                loginDao.getPassword()));

                User user = userRepository
                                .findByUsername(
                                                loginDao.getUsername())
                                .orElseThrow();

                UserDetails userDetails = new com.example.demo.security.CustomUserDetails(user);

                String token = jwtService.generateToken(
                                userDetails);

                return new LoginResponseDao(
                                token,
                                user.getUsername(),
                                user.getRole()
                                                .getRoleName());
        }
}