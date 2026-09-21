package com.privatedocs.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "01234567890123456789012345678901";
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
    }

    @Test
    void generatesAndReadsTokenSubject() {
        String token = jwtService.generateToken("owner@example.com");

        assertEquals("owner@example.com", jwtService.extractEmail(token));
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void rejectsMalformedAndTamperedTokens() {
        String token = jwtService.generateToken("owner@example.com");
        String tamperedToken = token.substring(0, token.length() - 1) + "x";

        assertFalse(jwtService.isTokenValid("not-a-jwt"));
        assertFalse(jwtService.isTokenValid(tamperedToken));
    }

    @Test
    void rejectsSecretsShorterThanThirtyTwoCharacters() {
        ReflectionTestUtils.setField(jwtService, "secret", "too-short");

        assertThrows(IllegalStateException.class, () -> jwtService.generateToken("owner@example.com"));
    }
}