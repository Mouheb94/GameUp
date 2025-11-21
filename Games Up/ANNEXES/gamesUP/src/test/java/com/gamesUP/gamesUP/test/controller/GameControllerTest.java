
package com.gamesUP.gamesUP.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.GameDTO;
import com.gamesUP.gamesUP.security.JwtAuthenticationFilter;
import com.gamesUP.gamesUP.security.JwtRequestFilter;
import com.gamesUP.gamesUP.service.GameService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.gamesUP.gamesUP.controller.GameController.class)
@AutoConfigureMockMvc(addFilters = false)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameService gameService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    void shouldCreateGame_whenValid_thenCreated() throws Exception {
        GameDTO in = GameDTO.builder().nom("Good Game").authorId(1L).genre("Action").build();
        GameDTO saved = GameDTO.builder().id(1L).nom("Good Game").authorId(1L).genre("Action").build();
        when(gameService.create(any(GameDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/games/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Good Game"));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidPayload() throws Exception {
        GameDTO invalid = GameDTO.builder().nom("X").authorId(1L).genre("Action").build();

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFound_whenUpdateMissing() throws Exception {
        Long missing = 99L;
        GameDTO update = GameDTO.builder().nom("Valid title").authorId(1L).genre("Action").build();
        when(gameService.update(eq(missing), any(GameDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        mockMvc.perform(put("/api/games/{id}", missing)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFound_whenDeleteMissing() throws Exception {
        Long missing = 100L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(gameService).delete(missing);

        mockMvc.perform(delete("/api/games/{id}", missing))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindAll_whenNomParam_thenUseFindByNomContaining() throws Exception {
        GameDTO g = GameDTO.builder().id(1L).nom("Match").authorId(1L).genre("Action").build();
        when(gameService.findByNomContaining("match")).thenReturn(List.of(g));

        mockMvc.perform(get("/api/games").param("nom", "match"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nom").value("Match"));

        verify(gameService, times(1)).findByNomContaining("match");
        verifyNoMoreInteractions(gameService);
    }

    @Test
    void shouldFindAll_whenGenreParam_thenUseFindByGenre() throws Exception {
        GameDTO g = GameDTO.builder().id(2L).nom("G2").authorId(2L).genre("RPG").build();
        when(gameService.findByGenre("RPG")).thenReturn(List.of(g));

        mockMvc.perform(get("/api/games").param("genre", "RPG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].genre").value("RPG"));

        verify(gameService, times(1)).findByGenre("RPG");
        verifyNoMoreInteractions(gameService);
    }

    @Test
    void shouldFindAll_whenAuthorIdParam_thenUseFindByAuthorId() throws Exception {
        GameDTO g = GameDTO.builder().id(3L).nom("G3").authorId(5L).genre("Sim").build();
        when(gameService.findByAuthorId(5L)).thenReturn(List.of(g));

        mockMvc.perform(get("/api/games").param("authorId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].authorId").value(5));

        verify(gameService, times(1)).findByAuthorId(5L);
        verifyNoMoreInteractions(gameService);
    }

    @Test
    void shouldFindAll_whenNoParam_thenUseFindAll() throws Exception {
        GameDTO g1 = GameDTO.builder().id(4L).nom("G4").authorId(1L).genre("A").build();
        when(gameService.findAll()).thenReturn(List.of(g1));

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4));

        verify(gameService, times(1)).findAll();
        verifyNoMoreInteractions(gameService);
    }
}
