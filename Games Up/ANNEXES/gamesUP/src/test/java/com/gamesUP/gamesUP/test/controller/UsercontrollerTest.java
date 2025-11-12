package com.gamesUP.gamesUP.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.UserDTO;
import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.enumeration.Role;
import com.gamesUP.gamesUP.security.JwtAuthenticationFilter;
import com.gamesUP.gamesUP.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.gamesUP.gamesUP.controller.UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsercontrollerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    // Ajout du MockBean pour le filtre JWT utilisé par le contexte de sécurité
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldSignUp_whenValid_thenOk() throws Exception {
        UserDTO in = UserDTO.builder()
                .nom("Alice")
                .email("alice@example.com")
                .password("passwd123")
                .role(Role.CUSTOMER)
                .build();

        User saved = User.builder()
                .id(5L)
                .nom("Alice")
                .email("alice@example.com")
                .password("encoded")
                .role(Role.CUSTOMER)
                .build();

        when(userService.createUser(any(UserDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/gamesUP/users/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.nom").value("Alice"));
        verify(userService).createUser(any(UserDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenServiceReportsBadRequest() throws Exception {
        String invalidJson = "{}";
        when(userService.createUser(any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid"));

        mockMvc.perform(post("/gamesUP/users/signup")
                        .contentType("application/json")
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
        verify(userService).createUser(any());
    }

    @Test
    void shouldReturnConflict_whenEmailExists() throws Exception {
        UserDTO in = UserDTO.builder()
                .nom("Bob")
                .email("bob@example.com")
                .password("pass1234")
                .role(Role.CUSTOMER)
                .build();

        when(userService.createUser(any(UserDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "email exists"));

        mockMvc.perform(post("/gamesUP/users/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isConflict());
        verify(userService).createUser(any(UserDTO.class));
    }
}
