// java
package com.gamesUP.gamesUP.test.service;

import com.gamesUP.gamesUP.dto.AuthorDTO;
import com.gamesUP.gamesUP.entity.Author;
import com.gamesUP.gamesUP.repository.AuthorRepository;
import com.gamesUP.gamesUP.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void update_whenExists_returnsUpdatedDto() {
        Long id = 1L;
        Author existing = Author.builder().id(id).name("Old").build();
        Author saved = Author.builder().id(id).name("New").build();

        when(authorRepository.findById(id)).thenReturn(Optional.of(existing));
        when(authorRepository.save(existing)).thenReturn(saved);

        AuthorDTO in = AuthorDTO.builder().name("New").build();
        AuthorDTO result = authorService.update(id, in);

        assertEquals(id, result.getId());
        assertEquals("New", result.getName());
        verify(authorRepository).findById(id);
        verify(authorRepository).save(existing);
    }

    @Test
    void update_whenNotFound_throws() {
        Long id = 2L;
        when(authorRepository.findById(id)).thenReturn(Optional.empty());

        AuthorDTO in = AuthorDTO.builder().name("X").build();
        assertThrows(RuntimeException.class, () -> authorService.update(id, in));

        verify(authorRepository).findById(id);
        verify(authorRepository, never()).save(any());
    }

    @Test
    void delete_whenExists_deletes() {
        Long id = 3L;
        when(authorRepository.existsById(id)).thenReturn(true);

        authorService.delete(id);

        verify(authorRepository).existsById(id);
        verify(authorRepository).deleteById(id);
    }

    @Test
    void delete_whenNotExists_throws() {
        Long id = 4L;
        when(authorRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authorService.delete(id));

        verify(authorRepository).existsById(id);
        verify(authorRepository, never()).deleteById(anyLong());
    }

    @Test
    void findById_whenFound_returnsDto() {
        Long id = 5L;
        Author a = Author.builder().id(id).name("Author 5").build();
        when(authorRepository.findById(id)).thenReturn(Optional.of(a));

        AuthorDTO dto = authorService.findById(id);

        assertEquals(id, dto.getId());
        assertEquals("Author 5", dto.getName());
        verify(authorRepository).findById(id);
    }

    @Test
    void findById_whenNotFound_throws() {
        Long id = 6L;
        when(authorRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authorService.findById(id));

        verify(authorRepository).findById(id);
    }

    @Test
    void findAll_returnsList() {
        Author a1 = Author.builder().id(7L).name("A1").build();
        when(authorRepository.findAll()).thenReturn(List.of(a1));

        var list = authorService.findAll();

        assertEquals(1, list.size());
        assertEquals("A1", list.get(0).getName());
        verify(authorRepository).findAll();
    }
}
