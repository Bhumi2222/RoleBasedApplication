package com.example.demo.security;

import com.example.demo.Entity.ApiPermission;
import com.example.demo.repository.ApiPermissionRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DynamicAuthorizationFilter extends OncePerRequestFilter {

        private final ApiPermissionRepository apiPermissionRepository;

        @Override
        protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                String path = request.getRequestURI();
                String method = request.getMethod();

                // Login API does not require permission
                if (path.startsWith("/api/auth/")) {
                        filterChain.doFilter(request, response);
                        return;
                }
                if (path.startsWith("/api/events/")) {
                        filterChain.doFilter(request, response);
                        return;
                }

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null ||
                                !authentication.isAuthenticated()) {

                        response.setStatus(
                                        HttpServletResponse.SC_UNAUTHORIZED);

                        response.getWriter()
                                        .write("Unauthorized");

                        return;
                }

                ApiPermission apiPermission = apiPermissionRepository
                                .findByApiPathAndHttpMethodAndIsActive(
                                                path,
                                                method,
                                                'Y')
                                .orElse(null);

                // API is not configured in database
                if (apiPermission == null) {

                        response.setStatus(
                                        HttpServletResponse.SC_FORBIDDEN);

                        response.getWriter()
                                        .write("API permission not configured");

                        return;
                }

                String requiredPermission = apiPermission
                                .getPermission()
                                .getPermissionCode();
                System.out.println("Required Permission: " + requiredPermission);
                System.out.println("User Permissions: " + authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority).toList());
                boolean hasPermission = authentication
                                .getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .anyMatch(authority -> authority.equals(
                                                requiredPermission));

                if (!hasPermission) {

                        response.setStatus(
                                        HttpServletResponse.SC_FORBIDDEN);

                        response.getWriter()
                                        .write("You do not have permission to access this API");

                        return;
                }

                filterChain.doFilter(request, response);
        }
}