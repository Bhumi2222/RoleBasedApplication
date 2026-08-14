package com.example.demo.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.Entity.User;

public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Set<String> permissionCodes;

    public CustomUserDetails(
            User user,
            Set<String> permissionCodes) {

        this.user = user;
        this.permissionCodes = permissionCodes != null
                ? permissionCodes
                : Set.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> authorities = new HashSet<>();

        if (user.getRole() != null) {

            String roleName = user.getRole().getRoleName();
            String normalizedRole = roleName == null
                    ? null
                    : roleName.trim().replaceFirst("^ROLE_", "");

            if (normalizedRole != null && !normalizedRole.isBlank()) {
                authorities.add(
                        new SimpleGrantedAuthority(
                                "ROLE_" + normalizedRole));
                authorities.add(
                        new SimpleGrantedAuthority(normalizedRole));
            }
        }

        // Permission authorities
        for (String permissionCode : permissionCodes) {

            authorities.add(
                    new SimpleGrantedAuthority(
                            permissionCode));
        }

        return authorities;
    }

    public UUID getUserId() {
        return user.getId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getIsActive() == null
                || user.getIsActive() == 'Y';
    }
}