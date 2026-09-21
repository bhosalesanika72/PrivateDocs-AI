package com.privatedocs.backend.service;

import com.privatedocs.backend.entity.User;
import com.privatedocs.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerEncodesPasswordAndSavesUser() {
        User savedUser = new User("Name", "name@example.com", "encoded");
        when(userRepository.existsByEmail("name@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(savedUser);

        User result = authService.register("Name", "name@example.com", "plain");

        assertSame(savedUser, result);
        verify(passwordEncoder).encode("plain");
        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(user ->
                user.getName().equals("Name") && user.getEmail().equals("name@example.com") && user.getPassword().equals("encoded")));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByEmail("name@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register("Name", "name@example.com", "plain"));

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    void loginReturnsUserWhenPasswordMatches() {
        User user = new User("Name", "name@example.com", "encoded");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain", "encoded")).thenReturn(true);

        assertSame(user, authService.login(user.getEmail(), "plain"));
    }

    @Test
    void loginRejectsUnknownUserOrWrongPassword() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService.login("missing@example.com", "plain"));

        User user = new User("Name", "name@example.com", "encoded");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> authService.login(user.getEmail(), "wrong"));
    }
}