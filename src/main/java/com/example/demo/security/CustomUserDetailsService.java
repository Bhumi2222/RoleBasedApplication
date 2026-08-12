package com.example.demo.security;

import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.User;
import com.example.demo.repository.RolePermissionRepository;
import com.example.demo.repository.UserRepository;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public CustomUserDetailsService(
            UserRepository userRepository,
            RolePermissionRepository rolePermissionRepository) {

        this.userRepository = userRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found"));

        Set<String> permissionCodes = rolePermissionRepository
                .findPermissionCodesByRoleId(
                        user.getRole().getId());

        return new CustomUserDetails(user, permissionCodes);
    }
}