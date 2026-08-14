package com.example.demo.serviceImpl;

import com.example.demo.dao.LoginDao;
import com.example.demo.dao.LoginResponseDao;
import com.example.demo.dao.PermissionDao;
import com.example.demo.dao.UserDao;
import com.example.demo.Entity.Permission;
import com.example.demo.Entity.User;
import com.example.demo.repository.RolePermissionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.JwtService;
import com.example.demo.service.AuthService;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        public UserDao getCurrentUser() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null ||
                                !authentication.isAuthenticated()) {

                        throw new RuntimeException(
                                        "User is not authenticated");
                }

                String username = authentication.getName();

                User user = userRepository
                                .findByUsername(username)
                                .orElseThrow(() -> new RuntimeException(
                                                "User not found"));

                return mapToUserDao(user);
        }

        private UserDao mapToUserDao(User user) {

                UserDao dao = new UserDao();

                dao.setId(user.getId());
                dao.setUsername(user.getUsername());
                // dao.setEmail(user.getEmail());

                dao.setRoleName(user.getRole().getRoleName());
                dao.setRoleId(user.getRole().getId());
                dao.setPermissions(rolePermissionRepository
                                .findPermissionIdsByRoleId(user.getRole().getId())
                                .stream().map(permission -> Builder(permission))
                                .collect(Collectors.toSet()));

                dao.setIsActive(user.getIsActive());
                return dao;
        }

        private PermissionDao Builder(Permission permission) {
                return PermissionDao.builder()
                                .id(permission.getId())
                                .permissionCode(permission.getPermissionCode())
                                .permissionName(permission.getPermissionName())
                                .moduleId(permission.getModule().getId())
                                .isActive(permission.getIsActive())
                                .build();
        }

}