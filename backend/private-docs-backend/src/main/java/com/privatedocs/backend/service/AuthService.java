package com.privatedocs.backend.service;

import com.privatedocs.backend.entity.User;
import com.privatedocs.backend.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // REGISTER USER
    // =========================
    public User register(String name, String email, String password) {

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        User user = new User();

        user.setName(name);
        user.setEmail(email);

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    // =========================
    // LOGIN USER
    // =========================
    public User login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        // Check password
        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }
}