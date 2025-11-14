// java
package com.gamesUP.gamesUP.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.WishlistDTO;
import com.gamesUP.gamesUP.security.JwtAuthenticationFilter;
import com.gamesUP.gamesUP.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.gamesUP.gamesUP.controller.WishlistController.class)
@AutoConfigureMockMvc(addFilters = false)
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WishlistService wishlistService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldCreate_whenValid_thenCreated() throws Exception {
        WishlistDTO in = WishlistDTO.builder()
                .userId(1L)
                .gameIds(Set.of(2L, 3L))
                .build();
        WishlistDTO saved = WishlistDTO.builder()
                .id(10L)
                .userId(1L)
                .gameIds(Set.of(2L, 3L))
                .build();

        when(wishlistService.create(any(WishlistDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/wishlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/wishlists/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userId").value(1));
        verify(wishlistService).create(any(WishlistDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidPayload() throws Exception {
        // userId manquant -> validation 400
        String invalid = "{\"gameIds\":[1,2]}";

        mockMvc.perform(post("/api/wishlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(wishlistService);
    }

    @Test
    void shouldCreate_whenServiceThrowsConflict_thenConflict() throws Exception {
        WishlistDTO in = WishlistDTO.builder().userId(1L).gameIds(Set.of(2L)).build();
        when(wishlistService.create(any(WishlistDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "conflict"));

        mockMvc.perform(post("/api/wishlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isConflict());
        verify(wishlistService).create(any(WishlistDTO.class));
    }

    @Test
    void shouldUpdate_whenSuccess_thenOk() throws Exception {
        Long id = 5L;
        WishlistDTO in = WishlistDTO.builder()
                .userId(2L)
                .gameIds(Set.of(4L))
                .build();
        WishlistDTO updated = WishlistDTO.builder()
                .id(id)
                .userId(2L)
                .gameIds(Set.of(4L))
                .build();

        when(wishlistService.update(eq(id), any(WishlistDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/wishlists/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.userId").value(2));
        verify(wishlistService).update(eq(id), any(WishlistDTO.class));
    }

    @Test
    void shouldUpdate_whenNotFound_thenNotFound() throws Exception {
        Long id = 99L;
        WishlistDTO in = WishlistDTO.builder().userId(2L).gameIds(Set.of(1L)).build();

        when(wishlistService.update(eq(id), any(WishlistDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mockMvc.perform(put("/api/wishlists/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound());
        verify(wishlistService).update(eq(id), any(WishlistDTO.class));
    }

    @Test
    void shouldDelete_whenSuccess_thenNoContent() throws Exception {
        Long id = 7L;
        doNothing().when(wishlistService).delete(id);

        mockMvc.perform(delete("/api/wishlists/{id}", id))
                .andExpect(status().isNoContent());
        verify(wishlistService).delete(id);
    }

    @Test
    void shouldDelete_whenNotFound_thenNotFound() throws Exception {
        Long id = 8L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found")).when(wishlistService).delete(id);

        mockMvc.perform(delete("/api/wishlists/{id}", id))
                .andExpect(status().isNotFound());
        verify(wishlistService).delete(id);
    }

    @Test
    void shouldFindById_whenFound_thenOk() throws Exception {
        Long id = 2L;
        WishlistDTO dto = WishlistDTO.builder().id(id).userId(5L).gameIds(Set.of(9L)).build();
        when(wishlistService.findById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/wishlists/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.userId").value(5))
                .andExpect(jsonPath("$.gameIds[0]").value(9));
        verify(wishlistService).findById(id);
    }

    @Test
    void shouldFindById_whenNotFound_thenNotFound() throws Exception {
        Long id = 33L;
        when(wishlistService.findById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mockMvc.perform(get("/api/wishlists/{id}", id))
                .andExpect(status().isNotFound());
        verify(wishlistService).findById(id);
    }

    @Test
    void shouldFindAll_thenOk() throws Exception {
        WishlistDTO a1 = WishlistDTO.builder().id(1L).userId(1L).gameIds(Set.of()).build();
        WishlistDTO a2 = WishlistDTO.builder().id(2L).userId(2L).gameIds(Set.of(3L)).build();
        when(wishlistService.findAll()).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/wishlists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].userId").value(2));
        verify(wishlistService).findAll();
    }

    @Test
    void shouldFindByUser_whenFound_thenOk() throws Exception {
        Long userId = 4L;
        WishlistDTO dto = WishlistDTO.builder().id(12L).userId(userId).gameIds(Set.of(7L)).build();
        when(wishlistService.findByUserId(userId)).thenReturn(dto);

        mockMvc.perform(get("/api/wishlists/by-user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.userId").value(4))
                .andExpect(jsonPath("$.gameIds[0]").value(7));
        verify(wishlistService).findByUserId(userId);
    }

    @Test
    void shouldFindByUser_whenNotFound_thenNotFound() throws Exception {
        Long userId = 99L;
        when(wishlistService.findByUserId(userId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mockMvc.perform(get("/api/wishlists/by-user/{userId}", userId))
                .andExpect(status().isNotFound());
        verify(wishlistService).findByUserId(userId);
    }
}
