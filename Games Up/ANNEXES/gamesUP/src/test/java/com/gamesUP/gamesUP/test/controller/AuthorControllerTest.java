
package com.gamesUP.gamesUP.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.AuthorDTO;
import com.gamesUP.gamesUP.security.JwtRequestFilter;
import com.gamesUP.gamesUP.service.AuthorService;
import com.gamesUP.gamesUP.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.gamesUP.gamesUP.controller.AuthorController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    void shouldCreateAuthor() throws Exception {
        AuthorDTO input = AuthorDTO.builder().name("Jane Doe").build();
        AuthorDTO saved = AuthorDTO.builder().id(1L).name("Jane Doe").build();

        when(authorService.create(any(AuthorDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/authors/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jane Doe"));

        verify(authorService).create(any(AuthorDTO.class));
    }

    @Test
    void shouldUpdateAuthor() throws Exception {
        Long id = 2L;
        AuthorDTO input = AuthorDTO.builder().name("Updated Name").build();
        AuthorDTO updated = AuthorDTO.builder().id(id).name("Updated Name").build();

        when(authorService.update(eq(id), any(AuthorDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/authors/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.name").value("Updated Name"));

        verify(authorService).update(eq(id), any(AuthorDTO.class));
    }

    @Test
    void shouldDeleteAuthor() throws Exception {
        Long id = 3L;
        doNothing().when(authorService).delete(id);

        mockMvc.perform(delete("/api/authors/{id}", id))
                .andExpect(status().isNoContent());

        verify(authorService).delete(id);
    }

    @Test
    void shouldFindById() throws Exception {
        Long id = 4L;
        AuthorDTO dto = AuthorDTO.builder().id(id).name("Author 4").build();

        when(authorService.findById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/authors/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.name").value("Author 4"));

        verify(authorService).findById(id);
    }

    @Test
    void shouldFindAll() throws Exception {
        AuthorDTO a1 = AuthorDTO.builder().id(1L).name("A1").build();
        AuthorDTO a2 = AuthorDTO.builder().id(2L).name("A2").build();

        when(authorService.findAll()).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("A1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("A2"));

        verify(authorService).findAll();
    }

    @Test
    void shouldReturnNotFoundWhenNotFound() throws Exception {
        Long missing = 99L;
        when(authorService.findById(missing)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found: " + missing));

        mockMvc.perform(get("/api/authors/{id}", missing))
                .andExpect(status().isNotFound());

        verify(authorService).findById(missing);
    }

    @Test
    void shouldReturnNotFoundOnUpdateWhenNotFound() throws Exception {
        Long missing = 100L;
        // nom valide (>= 2 caractères) pour éviter la validation 400
        AuthorDTO input = AuthorDTO.builder().name("XY").build();
        when(authorService.update(eq(missing), any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found: " + missing));

        mockMvc.perform(put("/api/authors/{id}", missing)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());

        verify(authorService).update(eq(missing), any());
    }

    @Test
    void shouldReturnNotFoundOnDeleteWhenNotFound() throws Exception {
        Long missing = 101L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found: " + missing)).when(authorService).delete(missing);

        mockMvc.perform(delete("/api/authors/{id}", missing))
                .andExpect(status().isNotFound());

        verify(authorService).delete(missing);
    }
}
