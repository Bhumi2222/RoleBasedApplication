package com.example.demo.serviceImpl;

import com.example.demo.dao.LoginDao;
import com.example.demo.dao.LoginResponseDao;
import com.example.demo.Entity.User;
import com.example.demo.repository.RolePermissionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.JwtService;
import com.example.demo.service.AuthService;

import java.util.Set;

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
        private final RolePermissionRepository rolePermissionRepository;

        public AuthServiceImpl(
                        AuthenticationManager authenticationManager,
                        UserRepository userRepository,
                        JwtService jwtService,
                        RolePermissionRepository rolePermissionRepository) {

                this.authenticationManager = authenticationManager;

                this.userRepository = userRepository;

                this.jwtService = jwtService;
                this.rolePermissionRepository = rolePermissionRepository;
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

                Set<String> permissionCodes = rolePermissionRepository
                                .findPermissionCodesByRoleId(
                                                user.getRole().getId());

                UserDetails userDetails = new CustomUserDetails(user, permissionCodes);

                String token = jwtService.generateToken(
                                userDetails);

                return new LoginResponseDao(
                                token,
                                user.getUsername(),
                                user.getRole().getRoleName(),
                                permissionCodes.stream().toList());
        }
}