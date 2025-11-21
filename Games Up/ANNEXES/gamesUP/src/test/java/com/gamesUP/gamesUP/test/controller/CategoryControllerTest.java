
package com.gamesUP.gamesUP.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.CategoryDTO;
import com.gamesUP.gamesUP.security.JwtAuthenticationFilter;
import com.gamesUP.gamesUP.security.JwtRequestFilter;
import com.gamesUP.gamesUP.service.CategoryService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.gamesUP.gamesUP.controller.CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    void shouldCreateCategory_whenValid_thenCreated() throws Exception {
        CategoryDTO in = CategoryDTO.builder().type("Strategy").build();
        CategoryDTO saved = CategoryDTO.builder().id(1L).type("Strategy").build();

        when(categoryService.create(any(CategoryDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/categories/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("Strategy"));
        verify(categoryService).create(any(CategoryDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidPayload() throws Exception {
        CategoryDTO invalid = CategoryDTO.builder().type("X").build();

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(categoryService);
    }

    @Test
    void shouldCreate_whenServiceThrowsConflict_thenConflict() throws Exception {
        CategoryDTO in = CategoryDTO.builder().type("Dup").build();
        when(categoryService.create(any(CategoryDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "exists"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isConflict());
        verify(categoryService).create(any(CategoryDTO.class));
    }

    @Test
    void shouldUpdate_whenSuccess_thenOk() throws Exception {
        Long id = 2L;
        CategoryDTO in = CategoryDTO.builder().type("Updated").build();
        CategoryDTO updated = CategoryDTO.builder().id(id).type("Updated").build();

        when(categoryService.update(eq(id), any(CategoryDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/categories/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.type").value("Updated"));
        verify(categoryService).update(eq(id), any(CategoryDTO.class));
    }

    @Test
    void shouldUpdate_whenNotFound_thenNotFound() throws Exception {
        Long id = 99L;
        CategoryDTO in = CategoryDTO.builder().type("Nope").build();
        when(categoryService.update(eq(id), any(CategoryDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mockMvc.perform(put("/api/categories/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound());
        verify(categoryService).update(eq(id), any(CategoryDTO.class));
    }

    @Test
    void shouldDelete_whenSuccess_thenNoContent() throws Exception {
        Long id = 3L;
        doNothing().when(categoryService).delete(id);

        mockMvc.perform(delete("/api/categories/{id}", id))
                .andExpect(status().isNoContent());
        verify(categoryService).delete(id);
    }

    @Test
    void shouldDelete_whenNotFound_thenNotFound() throws Exception {
        Long id = 4L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found")).when(categoryService).delete(id);

        mockMvc.perform(delete("/api/categories/{id}", id))
                .andExpect(status().isNotFound());
        verify(categoryService).delete(id);
    }

    @Test
    void shouldFindById_whenFound_thenOk() throws Exception {
        Long id = 5L;
        CategoryDTO dto = CategoryDTO.builder().id(id).type("Found").build();
        when(categoryService.findById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/categories/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.type").value("Found"));
        verify(categoryService).findById(id);
    }

    @Test
    void shouldFindById_whenNotFound_thenNotFound() throws Exception {
        Long id = 6L;
        when(categoryService.findById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mockMvc.perform(get("/api/categories/{id}", id))
                .andExpect(status().isNotFound());
        verify(categoryService).findById(id);
    }

    @Test
    void shouldFindAll_thenOk() throws Exception {
        CategoryDTO a1 = CategoryDTO.builder().id(1L).type("A").build();
        CategoryDTO a2 = CategoryDTO.builder().id(2L).type("B").build();
        when(categoryService.findAll()).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("A"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].type").value("B"));
        verify(categoryService).findAll();
    }
}
