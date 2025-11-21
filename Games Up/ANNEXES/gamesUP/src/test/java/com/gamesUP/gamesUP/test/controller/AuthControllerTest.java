// language: java
package com.gamesUP.gamesUP.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.AuthDTO;
import com.gamesUP.gamesUP.security.JwtAuthenticationFilter;
import com.gamesUP.gamesUP.security.JwtRequestFilter;
import com.gamesUP.gamesUP.service.AuthService;
import com.gamesUP.gamesUP.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.gamesUP.gamesUP.controller.AuthController.class,
        excludeAutoConfiguration = { SecurityAutoConfiguration.class })
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void shouldReturnToken_whenValidCredentials_thenOk() throws Exception {
        AuthDTO in = new AuthDTO("user@example.com", "securePass");
        String token = "ey.token.value";

        when(authService.login(eq("user@example.com"), eq("securePass"))).thenReturn(token);

        mockMvc.perform(post("/gamesUP/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(content().string(token));

        verify(authService).login(eq("user@example.com"), eq("securePass"));
    }

    @Test
    void shouldReturnBadRequest_whenEmailMissing_then400() throws Exception {
        String invalid = objectMapper.writeValueAsString(new AuthDTO("", "validPass"));

        mockMvc.perform(post("/gamesUP/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldReturnBadRequest_whenPasswordTooShort_then400() throws Exception {
        String invalid = objectMapper.writeValueAsString(new AuthDTO("user@example.com", "123"));

        mockMvc.perform(post("/gamesUP/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldReturnBadRequest_whenJsonMalformed_then400() throws Exception {
        String malformed = "{ \"email\": \"user@example.com\", \"password\": ";

        mockMvc.perform(post("/gamesUP/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformed))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldPropagateUnauthorized_whenServiceThrowsUnauthorized_then401() throws Exception {
        AuthDTO in = new AuthDTO("user@example.com", "wrongPass");

        when(authService.login(eq("user@example.com"), eq("wrongPass")))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials"));

        mockMvc.perform(post("/gamesUP/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isUnauthorized());

        verify(authService).login(eq("user@example.com"), eq("wrongPass"));
    }
}
