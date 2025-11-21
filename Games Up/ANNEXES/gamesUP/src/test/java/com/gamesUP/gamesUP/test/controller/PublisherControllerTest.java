
package com.gamesUP.gamesUP.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.dto.PublisherDTO;
import com.gamesUP.gamesUP.security.JwtAuthenticationFilter;
import com.gamesUP.gamesUP.security.JwtRequestFilter;
import com.gamesUP.gamesUP.service.PublisherService;
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

@WebMvcTest(controllers = com.gamesUP.gamesUP.controller.PublisherController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublisherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PublisherService publisherService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    void shouldCreatePublisher_whenValid_thenCreated() throws Exception {
        PublisherDTO in = PublisherDTO.builder().name("Ubisoft").build();
        PublisherDTO saved = PublisherDTO.builder().id(1L).name("Ubisoft").build();

        when(publisherService.create(any(PublisherDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/publishers/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ubisoft"));
        verify(publisherService).create(any(PublisherDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidPayload() throws Exception {
        PublisherDTO invalid = PublisherDTO.builder().name("X").build();

        mockMvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(publisherService);
    }

    @Test
    void shouldCreate_whenServiceThrowsConflict_thenConflict() throws Exception {
        PublisherDTO in = PublisherDTO.builder().name("Exists").build();
        when(publisherService.create(any(PublisherDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "exists"));

        mockMvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isConflict());
        verify(publisherService).create(any(PublisherDTO.class));
    }

    @Test
    void shouldUpdate_whenSuccess_thenOk() throws Exception {
        Long id = 2L;
        PublisherDTO in = PublisherDTO.builder().name("Updated").build();
        PublisherDTO updated = PublisherDTO.builder().id(id).name("Updated").build();

        when(publisherService.update(eq(id), any(PublisherDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/publishers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Updated"));
        verify(publisherService).update(eq(id), any(PublisherDTO.class));
    }

    @Test
    void shouldUpdate_whenNotFound_thenNotFound() throws Exception {
        Long id = 99L;
        PublisherDTO in = PublisherDTO.builder().name("Nope").build();
        when(publisherService.update(eq(id), any(PublisherDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mockMvc.perform(put("/api/publishers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound());
        verify(publisherService).update(eq(id), any(PublisherDTO.class));
    }

    @Test
    void shouldDelete_whenSuccess_thenNoContent() throws Exception {
        Long id = 3L;
        doNothing().when(publisherService).delete(id);

        mockMvc.perform(delete("/api/publishers/{id}", id))
                .andExpect(status().isNoContent());
        verify(publisherService).delete(id);
    }

    @Test
    void shouldDelete_whenNotFound_thenNotFound() throws Exception {
        Long id = 4L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found")).when(publisherService).delete(id);

        mockMvc.perform(delete("/api/publishers/{id}", id))
                .andExpect(status().isNotFound());
        verify(publisherService).delete(id);
    }

    @Test
    void shouldFindById_whenFound_thenOk() throws Exception {
        Long id = 5L;
        PublisherDTO dto = PublisherDTO.builder().id(id).name("Found").build();
        when(publisherService.findById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/publishers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Found"));
        verify(publisherService).findById(id);
    }

    @Test
    void shouldFindById_whenNotFound_thenNotFound() throws Exception {
        Long id = 6L;
        when(publisherService.findById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));

        mockMvc.perform(get("/api/publishers/{id}", id))
                .andExpect(status().isNotFound());
        verify(publisherService).findById(id);
    }

    @Test
    void shouldFindAll_thenOk() throws Exception {
        PublisherDTO a1 = PublisherDTO.builder().id(1L).name("A").build();
        PublisherDTO a2 = PublisherDTO.builder().id(2L).name("B").build();
        when(publisherService.findAll()).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/publishers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("A"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("B"));
        verify(publisherService).findAll();
    }
}
