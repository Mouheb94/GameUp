package com.gamesUP.gamesUP.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.PurchaseDTO;
import com.gamesUP.gamesUP.dto.PurchaseLineDTO;
import com.gamesUP.gamesUP.security.JwtAuthenticationFilter;
import com.gamesUP.gamesUP.security.JwtRequestFilter;
import com.gamesUP.gamesUP.service.PurchaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.gamesUP.gamesUP.controller.PurchaseController.class)
@AutoConfigureMockMvc(addFilters = false)
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PurchaseService purchaseService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    void shouldCreatePurchase_whenValid_thenCreated() throws Exception {
        PurchaseLineDTO line = PurchaseLineDTO.builder().gameId(2L).quantity(3).build();
        PurchaseDTO in = PurchaseDTO.builder()
                .userId(1L)
                .lines(List.of(line))
                .date(new Date())
                .paid(true)
                .delivered(false)
                .archived(false)
                .build();

        PurchaseLineDTO savedLine = PurchaseLineDTO.builder().id(100L).gameId(2L).quantity(3).build();
        PurchaseDTO saved = PurchaseDTO.builder()
                .id(10L)
                .userId(1L)
                .lines(List.of(savedLine))
                .paid(true)
                .delivered(false)
                .archived(false)
                .build();

        when(purchaseService.create(any(PurchaseDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/purchases/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.lines[0].gameId").value(2));
        verify(purchaseService).create(any(PurchaseDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidPayload() throws Exception {
        // payload invalide : manque userId et lines -> validation 400
        String invalidJson = "{}";

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(purchaseService);
    }

    @Test
    void shouldCreate_whenServiceThrowsNotFound_thenNotFound() throws Exception {
        PurchaseLineDTO line = PurchaseLineDTO.builder().gameId(99L).quantity(1).build();
        PurchaseDTO in = PurchaseDTO.builder()
                .userId(1L)
                .lines(List.of(line))
                .build();

        when(purchaseService.create(any(PurchaseDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "user or game not found"));

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound());
        verify(purchaseService).create(any(PurchaseDTO.class));
    }

    @Test
    void shouldUpdate_whenSuccess_thenOk() throws Exception {
        Long id = 5L;
        PurchaseLineDTO line = PurchaseLineDTO.builder().gameId(3L).quantity(4).build();
        PurchaseDTO in = PurchaseDTO.builder()
                .userId(2L)
                .lines(List.of(line))
                .paid(true)
                .delivered(true)
                .archived(false)
                .build();

        PurchaseLineDTO updatedLine = PurchaseLineDTO.builder().id(50L).gameId(3L).quantity(4).build();
        PurchaseDTO updated = PurchaseDTO.builder()
                .id(id)
                .userId(2L)
                .lines(List.of(updatedLine))
                .paid(true)
                .delivered(true)
                .archived(false)
                .build();

        when(purchaseService.update(eq(id), any(PurchaseDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/purchases/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.lines[0].gameId").value(3));
        verify(purchaseService).update(eq(id), any(PurchaseDTO.class));
    }

    @Test
    void shouldUpdate_whenNotFound_thenNotFound() throws Exception {
        Long id = 99L;
        PurchaseDTO in = PurchaseDTO.builder()
                .userId(1L)
                .lines(List.of(PurchaseLineDTO.builder().gameId(1L).quantity(1).build()))
                .build();

        when(purchaseService.update(eq(id), any(PurchaseDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mockMvc.perform(put("/api/purchases/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound());
        verify(purchaseService).update(eq(id), any(PurchaseDTO.class));
    }

    @Test
    void shouldDelete_whenSuccess_thenNoContent() throws Exception {
        Long id = 7L;
        doNothing().when(purchaseService).delete(id);

        mockMvc.perform(delete("/api/purchases/{id}", id))
                .andExpect(status().isNoContent());
        verify(purchaseService).delete(id);
    }

    @Test
    void shouldDelete_whenNotFound_thenNotFound() throws Exception {
        Long id = 8L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found")).when(purchaseService).delete(id);

        mockMvc.perform(delete("/api/purchases/{id}", id))
                .andExpect(status().isNotFound());
        verify(purchaseService).delete(id);
    }

    @Test
    void shouldFindById_whenFound_thenOk() throws Exception {
        Long id = 2L;
        PurchaseLineDTO line = PurchaseLineDTO.builder().id(33L).gameId(9L).quantity(2).build();
        PurchaseDTO dto = PurchaseDTO.builder()
                .id(id)
                .userId(5L)
                .lines(List.of(line))
                .paid(false)
                .delivered(false)
                .archived(false)
                .build();

        when(purchaseService.findById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/purchases/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.userId").value(5))
                .andExpect(jsonPath("$.lines[0].gameId").value(9));
        verify(purchaseService).findById(id);
    }

    @Test
    void shouldFindById_whenNotFound_thenNotFound() throws Exception {
        Long id = 33L;
        when(purchaseService.findById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mockMvc.perform(get("/api/purchases/{id}", id))
                .andExpect(status().isNotFound());
        verify(purchaseService).findById(id);
    }

    @Test
    void shouldFindAll_thenOk() throws Exception {
        PurchaseDTO a1 = PurchaseDTO.builder().id(1L).userId(1L).lines(List.of()).build();
        PurchaseDTO a2 = PurchaseDTO.builder().id(2L).userId(2L).lines(List.of()).build();
        when(purchaseService.findAll()).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/purchases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].userId").value(2));
        verify(purchaseService).findAll();
    }
}
