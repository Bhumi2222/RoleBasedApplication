package com.example.demo.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

        @Value("${app.jwt.secret}")
        private String secret;

        @Value("${app.jwt.expiration}")
        private long expiration;

        private Key getKey() {
                return Keys.hmacShaKeyFor(
                                secret.getBytes());
        }

        public String generateToken(
                        UserDetails userDetails) {

                return Jwts.builder()
                                .setSubject(
                                                userDetails.getUsername())
                                .setIssuedAt(new Date())
                                .setExpiration(
                                                new Date(
                                                                System.currentTimeMillis()
                                                                                + expiration))
                                .signWith(
                                                getKey(),
                                                SignatureAlgorithm.HS256)
                                .compact();
        }

        public String extractUsername(
                        String token) {

                return getClaims(token)
                                .getSubject();
        }

        public boolean isTokenValid(
                        String token,
                        UserDetails userDetails) {

                String username = extractUsername(token);

                return username.equals(
                                userDetails.getUsername())
                                && !isTokenExpired(token);
        }

        private boolean isTokenExpired(
                        String token) {

                return getClaims(token)
                                .getExpiration()
                                .before(new Date());
        }

        private Claims getClaims(
                        String token) {

                return Jwts.parserBuilder()
                                .setSigningKey(getKey())
                                .build()
                                .parseClaimsJws(token)
                                .getBody();
        }
}