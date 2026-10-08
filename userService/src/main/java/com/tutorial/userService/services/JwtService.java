package com.tutorial.userService.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.tutorial.userService.dto.JwtResult;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService  {
    private final SecretKey SECRET_KEY=Keys.hmacShaKeyFor("ao3whu7dwa0wvu5nwyhabv23tq9u48b32hu3nhbw".getBytes());
    private final long EXPIRATION_TIME = 86400000;

    public String generateToken(String email) {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + EXPIRATION_TIME;
        return Jwts.builder()
                .claim("sub", email)
                .claim("iat", new Date(nowMillis))
                .claim("exp", new Date(expMillis))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String getUsernameFromToken(String token) throws AccessDeniedException  {
        JwtResult result = validateToken(token);
        return result.isValid() ? result.getEmail() : null;
    }

    public boolean isTokenValid(String token, String username) throws AccessDeniedException  {
        String extractedUsername = getUsernameFromToken(token);
        System.out.println(extractedUsername);
        System.out.println(username);
        return extractedUsername != null && extractedUsername.equals(username);
    } 

    public JwtResult validateToken(String token) throws AccessDeniedException {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String username = claims.get("sub", String.class);

            return new JwtResult(true, username, null);
        } catch (Exception e) {
            throw new AccessDeniedException(e.getLocalizedMessage());
        }
    }
}