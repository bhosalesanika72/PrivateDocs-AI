package com.privatedocs.backend.controller;

import com.privatedocs.backend.entity.User;
import com.privatedocs.backend.security.JwtService;
import com.privatedocs.backend.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(
            AuthService authService,
            JwtService jwtService) {

        this.authService = authService;
        this.jwtService = jwtService;
    }

    // =========================
    // REGISTER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {

            if (request.getName() == null ||
                    request.getName().trim().isEmpty()) {

                return ResponseEntity.badRequest()
                        .body("Name is required");
            }

            if (request.getEmail() == null ||
                    request.getEmail().trim().isEmpty()) {

                return ResponseEntity.badRequest()
                        .body("Email is required");
            }

            if (request.getPassword() == null ||
                    request.getPassword().length() < 6) {

                return ResponseEntity.badRequest()
                        .body("Password must be at least 6 characters");
            }

            User user = authService.register(
                    request.getName().trim(),
                    request.getEmail().trim().toLowerCase(),
                    request.getPassword()
            );

            return ResponseEntity.ok(
                    new AuthResponse(
                            "Registration successful",
                            null,
                            user.getName(),
                            user.getEmail()
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {

            if (request.getEmail() == null ||
                    request.getPassword() == null) {

                return ResponseEntity.badRequest()
                        .body("Email and password are required");
            }

            User user = authService.login(
                    request.getEmail().trim().toLowerCase(),
                    request.getPassword()
            );

            String token =
                    jwtService.generateToken(user.getEmail());

            return ResponseEntity.ok(
                    new AuthResponse(
                            "Login successful",
                            token,
                            user.getName(),
                            user.getEmail()
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.status(401)
                    .body(e.getMessage());
        }
    }

    // =========================
    // REGISTER REQUEST
    // =========================
    public static class RegisterRequest {

        private String name;
        private String email;
        private String password;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // =========================
    // LOGIN REQUEST
    // =========================
    public static class LoginRequest {

        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // =========================
    // AUTH RESPONSE
    // =========================
    public static class AuthResponse {

        private String message;
        private String token;
        private String name;
        private String email;

        public AuthResponse(
                String message,
                String token,
                String name,
                String email) {

            this.message = message;
            this.token = token;
            this.name = name;
            this.email = email;
        }

        public String getMessage() {
            return message;
        }

        public String getToken() {
            return token;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}