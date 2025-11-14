
package com.gamesUP.gamesUP.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.PurchaseLineDTO;
import com.gamesUP.gamesUP.security.JwtAuthenticationFilter;
import com.gamesUP.gamesUP.service.PurchaseLineService;
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

@WebMvcTest(controllers = com.gamesUP.gamesUP.controller.PurchaseLineController.class)
@AutoConfigureMockMvc(addFilters = false)
class PurchaseLineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PurchaseLineService purchaseLineService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldCreateWithPurchase_whenValid_thenCreated() throws Exception {
        PurchaseLineDTO in = PurchaseLineDTO.builder()
                .gameId(1L)
                .quantity(3)
                .price(19.99)
                .build();
        PurchaseLineDTO saved = PurchaseLineDTO.builder()
                .id(10L)
                .gameId(1L)
                .quantity(3)
                .price(19.99)
                .build();

        when(purchaseLineService.create(eq(2L), any(PurchaseLineDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/purchase-lines")
                        .param("purchaseId", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/purchase-lines/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.gameId").value(1))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.price").value(19.99));
        verify(purchaseLineService).create(eq(2L), any(PurchaseLineDTO.class));
    }

    @Test
    void shouldCreateWithoutPurchase_whenValid_thenCreated() throws Exception {
        PurchaseLineDTO in = PurchaseLineDTO.builder()
                .gameId(4L)
                .quantity(1)
                .price(9.5)
                .build();
        PurchaseLineDTO saved = PurchaseLineDTO.builder()
                .id(11L)
                .gameId(4L)
                .quantity(1)
                .price(9.5)
                .build();

        when(purchaseLineService.create(eq(null), any(PurchaseLineDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/purchase-lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/purchase-lines/11"))
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.gameId").value(4));
        verify(purchaseLineService).create(eq(null), any(PurchaseLineDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidPayload() throws Exception {
        // missing gameId -> validation 400
        String invalid = "{\"quantity\":1,\"price\":5.0}";

        mockMvc.perform(post("/api/purchase-lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(purchaseLineService);
    }

    @Test
    void shouldCreate_whenServiceThrowsNotFound_thenNotFound() throws Exception {
        PurchaseLineDTO in = PurchaseLineDTO.builder()
                .gameId(99L)
                .quantity(2)
                .price(5.0)
                .build();

        when(purchaseLineService.create(any(), any(PurchaseLineDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "game not found"));

        mockMvc.perform(post("/api/purchase-lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound());
        verify(purchaseLineService).create(any(), any(PurchaseLineDTO.class));
    }

    @Test
    void shouldUpdate_whenSuccess_thenOk() throws Exception {
        Long id = 4L;
        PurchaseLineDTO in = PurchaseLineDTO.builder().gameId(2L).quantity(5).price(7.5).build();
        PurchaseLineDTO updated = PurchaseLineDTO.builder().id(id).gameId(2L).quantity(5).price(7.5).build();

        when(purchaseLineService.update(eq(id), eq(3L), any(PurchaseLineDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/purchase-lines/{id}", id)
                        .param("purchaseId", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.gameId").value(2))
                .andExpect(jsonPath("$.quantity").value(5));
        verify(purchaseLineService).update(eq(id), eq(3L), any(PurchaseLineDTO.class));
    }

    @Test
    void shouldUpdate_whenNotFound_thenNotFound() throws Exception {
        Long id = 99L;
        PurchaseLineDTO in = PurchaseLineDTO.builder().gameId(1L).quantity(1).price(1.0).build();

        when(purchaseLineService.update(eq(id), any(), any(PurchaseLineDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mockMvc.perform(put("/api/purchase-lines/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound());
        verify(purchaseLineService).update(eq(id), any(), any(PurchaseLineDTO.class));
    }

    @Test
    void shouldDelete_whenSuccess_thenNoContent() throws Exception {
        Long id = 7L;
        doNothing().when(purchaseLineService).delete(id);

        mockMvc.perform(delete("/api/purchase-lines/{id}", id))
                .andExpect(status().isNoContent());
        verify(purchaseLineService).delete(id);
    }

    @Test
    void shouldDelete_whenNotFound_thenNotFound() throws Exception {
        Long id = 8L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found")).when(purchaseLineService).delete(id);

        mockMvc.perform(delete("/api/purchase-lines/{id}", id))
                .andExpect(status().isNotFound());
        verify(purchaseLineService).delete(id);
    }

    @Test
    void shouldFindById_whenFound_thenOk() throws Exception {
        Long id = 2L;
        PurchaseLineDTO dto = PurchaseLineDTO.builder().id(id).gameId(4L).quantity(6).price(12.0).build();
        when(purchaseLineService.findById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/purchase-lines/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.gameId").value(4))
                .andExpect(jsonPath("$.quantity").value(6));
        verify(purchaseLineService).findById(id);
    }

    @Test
    void shouldFindById_whenNotFound_thenNotFound() throws Exception {
        Long id = 33L;
        when(purchaseLineService.findById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mockMvc.perform(get("/api/purchase-lines/{id}", id))
                .andExpect(status().isNotFound());
        verify(purchaseLineService).findById(id);
    }

    @Test
    void shouldFindAll_thenOk() throws Exception {
        PurchaseLineDTO a1 = PurchaseLineDTO.builder().id(1L).gameId(1L).quantity(2).price(3.0).build();
        PurchaseLineDTO a2 = PurchaseLineDTO.builder().id(2L).gameId(2L).quantity(4).price(6.0).build();
        when(purchaseLineService.findAll()).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/purchase-lines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].gameId").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].gameId").value(2));
        verify(purchaseLineService).findAll();
    }
}
