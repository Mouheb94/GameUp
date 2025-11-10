package com.gamesUP.gamesUP.testUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import com.gamesUP.gamesUP.dto.AuthorDTO;
import com.gamesUP.gamesUP.entity.Author;
import com.gamesUP.gamesUP.repository.AuthorRepository;
import com.gamesUP.gamesUP.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void shouldReturnAuthorWhenExists() {
        Author author = Author.builder()
                .id(1L)
                .name("Tolkien")
                .build();
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        AuthorDTO result = authorService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Tolkien", result.getName());
        verify(authorRepository).findById(1L);
    }

    @Test
    void shouldCreateAuthorSuccess() {
        String name = "Asimov";

        AuthorDTO dto = AuthorDTO.builder()
                .name(name)
                .build();

        Author saved = Author.builder()
                .id(2L)
                .name(name)
                .build();
        when(authorRepository.save(any(Author.class))).thenReturn(saved);

        AuthorDTO result = authorService.create(dto);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(name, result.getName());
        verify(authorRepository).save(any(Author.class));
    }
}
