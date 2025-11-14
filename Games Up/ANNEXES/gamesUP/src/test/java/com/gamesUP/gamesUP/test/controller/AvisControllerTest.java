package com.gamesUP.gamesUP.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.AvisDTO;
import com.gamesUP.gamesUP.security.JwtAuthenticationFilter;
import com.gamesUP.gamesUP.service.AvisService;
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
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.gamesUP.gamesUP.controller.AvisController.class)
@AutoConfigureMockMvc(addFilters = false)
class AvisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AvisService avisService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldCreateAvis_whenValid_thenCreated() throws Exception {
        AvisDTO in = AvisDTO.builder()
                .commentaire("Super jeu")
                .note(8)
                .gameId(3L)
                .build();
        AvisDTO saved = AvisDTO.builder()
                .id(1L)
                .commentaire("Super jeu")
                .note(8)
                .gameId(3L)
                .build();

        when(avisService.create(any(AvisDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/avis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/avis/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.commentaire").value("Super jeu"))
                .andExpect(jsonPath("$.gameId").value(3));
        verify(avisService).create(any(AvisDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidPayload() throws Exception {
        // commentaire trop court (min 2) -> validation 400
        AvisDTO invalid = AvisDTO.builder()
                .commentaire("X")
                .note(5)
                .gameId(1L)
                .build();

        mockMvc.perform(post("/api/avis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(avisService);
    }

    @Test
    void shouldUpdate_whenSuccess_thenOk() throws Exception {
        Long id = 4L;
        AvisDTO input = AvisDTO.builder()
                .commentaire("Maj")
                .note(7)
                .gameId(2L)
                .build();
        AvisDTO updated = AvisDTO.builder()
                .id(id)
                .commentaire("Maj")
                .note(7)
                .gameId(2L)
                .build();

        when(avisService.update(eq(id), any(AvisDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/avis/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.commentaire").value("Maj"));
        verify(avisService).update(eq(id), any(AvisDTO.class));
    }

    @Test
    void shouldUpdate_whenServiceThrows_thenServerError() throws Exception {
        Long id = 99L;
        AvisDTO input = AvisDTO.builder()
                .commentaire("Fail")
                .note(1)
                .gameId(1L)
                .build();

        when(avisService.update(eq(id), any(AvisDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "not found"));

        mockMvc.perform(put("/api/avis/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().is5xxServerError());
        verify(avisService).update(eq(id), any(AvisDTO.class));
    }

    @Test
    void shouldDelete_whenSuccess_thenNoContent() throws Exception {
        Long id = 7L;
        doNothing().when(avisService).delete(id);

        mockMvc.perform(delete("/api/avis/{id}", id))
                .andExpect(status().isNoContent());
        verify(avisService).delete(id);
    }

    @Test
    void shouldDelete_whenServiceThrows_thenServerError() throws Exception {
        Long id = 8L;
        doThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "not found")).when(avisService).delete(id);

        mockMvc.perform(delete("/api/avis/{id}", id))
                .andExpect(status().is5xxServerError());
        verify(avisService).delete(id);
    }

    @Test
    void shouldFindById_whenFound_thenOk() throws Exception {
        Long id = 2L;
        AvisDTO dto = AvisDTO.builder()
                .id(id)
                .commentaire("Found")
                .note(6)
                .gameId(4L)
                .build();
        when(avisService.findById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/avis/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.commentaire").value("Found"))
                .andExpect(jsonPath("$.gameId").value(4));
        verify(avisService).findById(id);
    }

    @Test
    void shouldFindById_whenNotFound_thenServerError() throws Exception {
        Long id = 33L;
        when(avisService.findById(id)).thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "not found"));

        mockMvc.perform(get("/api/avis/{id}", id))
                .andExpect(status().is5xxServerError());
        verify(avisService).findById(id);
    }

    @Test
    void shouldFindAll_thenOk() throws Exception {
        AvisDTO a1 = AvisDTO.builder().id(1L).commentaire("A").note(5).gameId(1L).build();
        AvisDTO a2 = AvisDTO.builder().id(2L).commentaire("B").note(6).gameId(2L).build();
        when(avisService.findAll()).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/avis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
        verify(avisService).findAll();
    }
}
