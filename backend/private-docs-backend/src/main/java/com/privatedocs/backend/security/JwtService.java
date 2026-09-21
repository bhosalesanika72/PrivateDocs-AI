package com.privatedocs.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${JWT_SECRET:}")
    private String secret;

    // Get secret key
    private SecretKey getSigningKey() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must contain at least 32 characters");
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // =========================
    // GENERATE TOKEN
    // =========================
    public String generateToken(String email) {

        long expirationTime = 1000L * 60 * 60 * 24; // 24 hours

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expirationTime
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }

    // =========================
    // EXTRACT EMAIL
    // =========================
    @SuppressWarnings("null")
    public String extractEmail(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    // =========================
    // EXTRACT CLAIM
    // =========================
    private <T> T extractClaim(
            String token,
            Function<Claims, T> resolver) {

        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return resolver.apply(claims);
    }

    // =========================
    // VALIDATE TOKEN
    // =========================
    @SuppressWarnings("null")
    public boolean isTokenValid(String token) {

        try {

            extractClaim(
                    token,
                    Claims::getSubject
            );

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}