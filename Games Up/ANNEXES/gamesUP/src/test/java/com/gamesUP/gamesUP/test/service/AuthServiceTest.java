package com.gamesUP.gamesUP.test.service;

import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.service.AuthService;
import com.gamesUP.gamesUP.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        String email = "user@example.com";
        String rawPassword = "plainPass";
        String encoded = "$2a$...";
        String expectedToken = "jwt.token.value";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encoded);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encoded)).thenReturn(true);
        when(jwtUtil.generateToken(email)).thenReturn(expectedToken);

        String token = authService.login(email, rawPassword);

        assertNotNull(token);
        assertEquals(expectedToken, token);

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(rawPassword, encoded);
        verify(jwtUtil).generateToken(email);
    }

    @Test
    void login_shouldThrowIllegalArgument_whenUserNotFound() {
        String email = "missing@example.com";
        String rawPassword = "whatever";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.login(email, rawPassword));

        assertTrue(ex.getMessage().contains(email));

        verify(userRepository).findByEmail(email);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_shouldThrowIllegalArgument_whenPasswordIsIncorrect() {
        String email = "user@example.com";
        String rawPassword = "wrongPass";
        String encoded = "$2a$...";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encoded);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encoded)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.login(email, rawPassword));

        assertTrue(ex.getMessage().toLowerCase().contains("incorrect") || ex.getMessage().toLowerCase().contains("password"));

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(rawPassword, encoded);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_shouldPropagateRuntimeException_whenRepositoryThrows() {
        String email = "db@err.com";
        String raw = "p";

        when(userRepository.findByEmail(email)).thenThrow(new RuntimeException("db failure"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(email, raw));
        assertTrue(ex.getMessage().contains("db failure"));

        verify(userRepository).findByEmail(email);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_shouldPropagateRuntimeException_whenPasswordEncoderThrows() {
        String email = "user@pe.com";
        String raw = "p";
        User user = new User();
        user.setEmail(email);
        user.setPassword("enc");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(raw, "enc")).thenThrow(new RuntimeException("encoder failure"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(email, raw));
        assertTrue(ex.getMessage().contains("encoder failure"));

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(raw, "enc");
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_shouldPropagateRuntimeException_whenJwtUtilThrows() {
        String email = "user@jwt.com";
        String raw = "p";
        User user = new User();
        user.setEmail(email);
        user.setPassword("enc");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(raw, "enc")).thenReturn(true);
        when(jwtUtil.generateToken(email)).thenThrow(new RuntimeException("jwt failure"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(email, raw));
        assertTrue(ex.getMessage().contains("jwt failure"));

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(raw, "enc");
        verify(jwtUtil).generateToken(email);
    }

    @Test
    void login_shouldReturnNull_whenJwtUtilReturnsNull() {
        String email = "user@nulltoken.com";
        String raw = "p";
        User user = new User();
        user.setEmail(email);
        user.setPassword("enc");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(raw, "enc")).thenReturn(true);
        when(jwtUtil.generateToken(email)).thenReturn(null);

        String token = authService.login(email, raw);

        assertNull(token);

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(raw, "enc");
        verify(jwtUtil).generateToken(email);
    }
}
