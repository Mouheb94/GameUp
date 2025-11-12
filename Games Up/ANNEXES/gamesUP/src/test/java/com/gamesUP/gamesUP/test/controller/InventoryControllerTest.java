
package com.gamesUP.gamesUP.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.InventoryDTO;
import com.gamesUP.gamesUP.security.JwtAuthenticationFilter;
import com.gamesUP.gamesUP.service.InventoryService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.gamesUP.gamesUP.controller.InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldCreateInventory_whenValid_thenCreated() throws Exception {
        InventoryDTO in = InventoryDTO.builder()
                .stock(Map.of(1L, 5, 2L, 3))
                .build();
        InventoryDTO saved = InventoryDTO.builder()
                .id(10L)
                .stock(Map.of(1L, 5, 2L, 3))
                .build();

        when(inventoryService.create(any(InventoryDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/inventories/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.stock.1").value(5))
                .andExpect(jsonPath("$.stock.2").value(3));
        verify(inventoryService).create(any(InventoryDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidPayload() throws Exception {
        // missing stock -> validation should fail (stock @NotNull)
        String invalidJson = "{}";

        mockMvc.perform(post("/api/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(inventoryService);
    }

    @Test
    void shouldUpdate_whenSuccess_thenOk() throws Exception {
        Long id = 4L;
        InventoryDTO input = InventoryDTO.builder().stock(Map.of(2L, 7)).build();
        InventoryDTO updated = InventoryDTO.builder().id(id).stock(Map.of(2L, 7)).build();

        when(inventoryService.update(eq(id), any(InventoryDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/inventories/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.stock.2").value(7));
        verify(inventoryService).update(eq(id), any(InventoryDTO.class));
    }

    @Test
    void shouldUpdate_whenNotFound_thenNotFound() throws Exception {
        Long id = 99L;
        InventoryDTO input = InventoryDTO.builder().stock(Map.of(1L, 1)).build();

        when(inventoryService.update(eq(id), any(InventoryDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mockMvc.perform(put("/api/inventories/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
        verify(inventoryService).update(eq(id), any(InventoryDTO.class));
    }

    @Test
    void shouldDelete_whenSuccess_thenNoContent() throws Exception {
        Long id = 7L;
        doNothing().when(inventoryService).delete(id);

        mockMvc.perform(delete("/api/inventories/{id}", id))
                .andExpect(status().isNoContent());
        verify(inventoryService).delete(id);
    }

    @Test
    void shouldDelete_whenNotFound_thenNotFound() throws Exception {
        Long id = 8L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found")).when(inventoryService).delete(id);

        mockMvc.perform(delete("/api/inventories/{id}", id))
                .andExpect(status().isNotFound());
        verify(inventoryService).delete(id);
    }

    @Test
    void shouldFindById_whenFound_thenOk() throws Exception {
        Long id = 2L;
        InventoryDTO dto = InventoryDTO.builder().id(id).stock(Map.of(4L, 6)).build();
        when(inventoryService.findById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/inventories/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.stock.4").value(6));
        verify(inventoryService).findById(id);
    }

    @Test
    void shouldFindById_whenNotFound_thenNotFound() throws Exception {
        Long id = 33L;
        when(inventoryService.findById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mockMvc.perform(get("/api/inventories/{id}", id))
                .andExpect(status().isNotFound());
        verify(inventoryService).findById(id);
    }

    @Test
    void shouldFindAll_thenOk() throws Exception {
        InventoryDTO a1 = InventoryDTO.builder().id(1L).stock(Map.of(1L, 4)).build();
        InventoryDTO a2 = InventoryDTO.builder().id(2L).stock(Map.of(2L, 6)).build();
        when(inventoryService.findAll()).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/inventories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].stock.1").value(4))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].stock.2").value(6));
        verify(inventoryService).findAll();
    }
}
