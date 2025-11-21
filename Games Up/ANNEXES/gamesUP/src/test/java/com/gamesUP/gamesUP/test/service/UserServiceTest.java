// language: java
package com.gamesUP.gamesUP.test.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.gamesUP.gamesUP.dto.UserDTO;
import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.enumeration.Role;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

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

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("Bob", captor.getValue().getNom());
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

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("email"));
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

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("password"));
        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldGetUserSuccess() {
        Long id = 10L;
        User u = User.builder().id(id).nom("Nom").email("a@b.com").build();
        when(userRepository.findById(id)).thenReturn(Optional.of(u));

        User result = userService.getUser(id);

        assertEquals(id, result.getId());
        assertEquals("a@b.com", result.getEmail());
        verify(userRepository).findById(id);
    }

    @Test
    void shouldThrowWhenGetUserNotFound() {
        Long id = 11L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.getUser(id));
        assertTrue(ex.getMessage().contains(String.valueOf(id)));
        verify(userRepository).findById(id);
    }

    @Test
    void shouldGetAllUsers() {
        User u1 = User.builder().id(1L).email("x@a.com").build();
        User u2 = User.builder().id(2L).email("y@a.com").build();
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        var list = userService.getAllUsers();

        assertEquals(2, list.size());
        verify(userRepository).findAll();
    }

    @Test
    void updateUser_fullUpdate_success() {
        Long id = 5L;
        User existing = User.builder().id(id).nom("Old").email("old@e.com").password("oldEnc").role(Role.CUSTOMER).build();
        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        String newEmail = "new@e.com";
        UserDTO dto = UserDTO.builder()
                .nom("NewName")
                .email(newEmail)
                .password("newPwd")
                .role(Role.ADMIN)
                .build();

        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(passwordEncoder.encode("newPwd")).thenReturn("newEnc");
        User saved = User.builder().id(id).nom("NewName").email(newEmail).password("newEnc").role(Role.ADMIN).build();
        when(userRepository.save(existing)).thenReturn(saved);

        User result = userService.updateUser(id, dto);

        assertEquals(newEmail, result.getEmail());
        assertEquals("NewName", result.getNom());
        assertEquals(Role.ADMIN, result.getRole());
        verify(userRepository).findById(id);
        verify(userRepository).existsByEmail(newEmail);
        verify(passwordEncoder).encode("newPwd");
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_emailConflict_throws() {
        Long id = 6L;
        User existing = User.builder().id(id).email("a@b.com").build();
        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        UserDTO dto = UserDTO.builder().email("taken@e.com").build();

        when(userRepository.existsByEmail("taken@e.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.updateUser(id, dto));
        assertTrue(ex.getMessage().toLowerCase().contains("email"));
        verify(userRepository).findById(id);
        verify(userRepository).existsByEmail("taken@e.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_partial_noBlankUpdates_skipsPasswordAndNomAndRole() {
        Long id = 7L;
        User existing = User.builder().id(id).nom("Keep").email("keep@e.com").password("enc").role(Role.CUSTOMER).build();
        when(userRepository.findById(id)).thenReturn(Optional.of(existing));

        UserDTO dto = UserDTO.builder()
                .nom("   ")
                .password("  ")
                .role(null)
                .email("keep@e.com") // same as existing
                .build();

        when(userRepository.save(existing)).thenReturn(existing);

        User result = userService.updateUser(id, dto);

        assertEquals("Keep", result.getNom());
        assertEquals("keep@e.com", result.getEmail());
        verify(userRepository).findById(id);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(existing);
    }

    @Test
    void deleteUser_exists_deletes() {
        Long id = 8L;
        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteUser(id);

        verify(userRepository).existsById(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    void deleteUser_notExists_throws() {
        Long id = 9L;
        when(userRepository.existsById(id)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(id));
        assertTrue(ex.getMessage().contains(String.valueOf(id)));
        verify(userRepository).existsById(id);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void findByEmail_returnsOptional() {
        String email = "find@e.com";
        User u = User.builder().id(12L).email(email).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(u));

        Optional<User> opt = userService.findByEmail(email);

        assertTrue(opt.isPresent());
        assertEquals(email, opt.get().getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void findByUsernameOrEmail_delegatesToFindByEmail() {
        String identifier = "delegate@e.com";
        User u = User.builder().id(13L).email(identifier).build();
        when(userRepository.findByEmail(identifier)).thenReturn(Optional.of(u));

        Optional<User> opt = userService.findByUsernameOrEmail(identifier);

        assertTrue(opt.isPresent());
        assertEquals(identifier, opt.get().getEmail());
        verify(userRepository).findByEmail(identifier);
    }
}
