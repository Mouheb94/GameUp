// java
package com.gamesUP.gamesUP.testUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.gamesUP.gamesUP.dto.PublisherDTO;
import com.gamesUP.gamesUP.entity.Publisher;
import com.gamesUP.gamesUP.repository.PublisherRepository;
import com.gamesUP.gamesUP.service.PublisherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private PublisherService publisherService;

    @Test
    void shouldCreatePublisherSuccess() {
        PublisherDTO dto = PublisherDTO.builder()
                .name("Ubisoft")
                .build();

        when(publisherRepository.findByName("Ubisoft")).thenReturn(Optional.empty());

        Publisher saved = Publisher.builder()
                .id(1L)
                .name("Ubisoft")
                .build();
        when(publisherRepository.save(any(Publisher.class))).thenReturn(saved);

        PublisherDTO result = publisherService.create(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ubisoft", result.getName());
        verify(publisherRepository).findByName("Ubisoft");
        verify(publisherRepository).save(any(Publisher.class));
    }

    @Test
    void shouldThrowWhenNameExistsOnCreate() {
        PublisherDTO dto = PublisherDTO.builder()
                .name("EA")
                .build();

        Publisher existing = Publisher.builder().id(2L).name("EA").build();
        when(publisherRepository.findByName("EA")).thenReturn(Optional.of(existing));

        assertThrows(RuntimeException.class, () -> publisherService.create(dto));
        verify(publisherRepository).findByName("EA");
        verify(publisherRepository, never()).save(any());
    }

    @Test
    void shouldUpdatePublisherSuccess() {
        Long id = 3L;
        Publisher existing = Publisher.builder().id(id).name("OldName").build();
        when(publisherRepository.findById(id)).thenReturn(Optional.of(existing));
        when(publisherRepository.findByName("NewName")).thenReturn(Optional.empty());

        Publisher saved = Publisher.builder().id(id).name("NewName").build();
        when(publisherRepository.save(any(Publisher.class))).thenReturn(saved);

        PublisherDTO dto = PublisherDTO.builder().name("NewName").build();

        PublisherDTO result = publisherService.update(id, dto);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("NewName", result.getName());
        verify(publisherRepository).findById(id);
        verify(publisherRepository).findByName("NewName");
        verify(publisherRepository).save(any(Publisher.class));
    }

    @Test
    void shouldThrowWhenNameExistsOnUpdate() {
        Long id = 4L;
        Publisher existing = Publisher.builder().id(id).name("Some").build();
        when(publisherRepository.findById(id)).thenReturn(Optional.of(existing));

        Publisher other = Publisher.builder().id(99L).name("Duplicate").build();
        when(publisherRepository.findByName("Duplicate")).thenReturn(Optional.of(other));

        PublisherDTO dto = PublisherDTO.builder().name("Duplicate").build();

        assertThrows(RuntimeException.class, () -> publisherService.update(id, dto));
        verify(publisherRepository).findById(id);
        verify(publisherRepository).findByName("Duplicate");
        verify(publisherRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUpdateNotFound() {
        Long id = 10L;
        when(publisherRepository.findById(id)).thenReturn(Optional.empty());
        PublisherDTO dto = PublisherDTO.builder().name("X").build();
        assertThrows(RuntimeException.class, () -> publisherService.update(id, dto));
        verify(publisherRepository).findById(id);
        verify(publisherRepository, never()).save(any());
    }

    @Test
    void shouldDeletePublisherSuccess() {
        Long id = 5L;
        when(publisherRepository.existsById(id)).thenReturn(true);

        publisherService.delete(id);

        verify(publisherRepository).existsById(id);
        verify(publisherRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        Long id = 6L;
        when(publisherRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> publisherService.delete(id));
        verify(publisherRepository).existsById(id);
        verify(publisherRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldFindByIdSuccess() {
        Long id = 7L;
        Publisher p = Publisher.builder().id(id).name("Penguin").build();
        when(publisherRepository.findById(id)).thenReturn(Optional.of(p));

        PublisherDTO dto = publisherService.findById(id);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Penguin", dto.getName());
        verify(publisherRepository).findById(id);
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        Long id = 8L;
        when(publisherRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> publisherService.findById(id));
        verify(publisherRepository).findById(id);
    }

    @Test
    void shouldFindAll() {
        Publisher p1 = Publisher.builder().id(1L).name("A").build();
        Publisher p2 = Publisher.builder().id(2L).name("B").build();
        when(publisherRepository.findAll()).thenReturn(List.of(p1, p2));

        var list = publisherService.findAll();

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals("A", list.get(0).getName());
        assertEquals(2L, list.get(1).getId());
        assertEquals("B", list.get(1).getName());
        verify(publisherRepository).findAll();
    }
}
