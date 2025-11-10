package com.gamesUP.gamesUP.testUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.gamesUP.gamesUP.dto.UserDTO;
import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.enumeration.Role;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserSuccess() {
        UserDTO dto = UserDTO.builder()
                .nom("Bob")
                .email("bob@example.com")
                .password("secret123")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedSecret");
        User saved = User.builder()
                .id(2L)
                .nom(dto.getNom())
                .email(dto.getEmail())
                .password("encodedSecret")
                .role(Role.CUSTOMER)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(dto.getEmail(), result.getEmail());
        verify(userRepository).existsByEmail(dto.getEmail());
        verify(passwordEncoder).encode(dto.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailExists() {
        UserDTO dto = UserDTO.builder()
                .nom("Alice")
                .email("alice@example.com")
                .password("pwd12345")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
        verify(userRepository).existsByEmail(dto.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPasswordBlank() {
        UserDTO dto = UserDTO.builder()
                .nom("Eve")
                .email("eve@example.com")
                .password("   ")
                .role(Role.CUSTOMER)
                .build();

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }
}
