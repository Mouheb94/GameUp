package com.gamesUP.gamesUP.testUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.gamesUP.gamesUP.dto.GameDTO;
import com.gamesUP.gamesUP.entity.Author;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.repository.AuthorRepository;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void shouldCreateGameWithAuthor() {
        Long authorId = 1L;
        GameDTO dto = GameDTO.builder()
                .nom("MonJeu")
                .genre("Action")
                .authorId(authorId)
                .build();

        Author author = Author.builder().id(authorId).build();
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        Game saved = Game.builder()
                .id(10L)
                .nom(dto.getNom())
                .genre(dto.getGenre())
                .author(author)
                .build();
        when(gameRepository.save(any(Game.class))).thenReturn(saved);

        GameDTO result = gameService.create(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("MonJeu", result.getNom());
        assertEquals("Action", result.getGenre());
        assertEquals(authorId, result.getAuthorId());
        verify(authorRepository).findById(authorId);
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void shouldThrowWhenAuthorNotFoundOnCreate() {
        Long authorId = 2L;
        GameDTO dto = GameDTO.builder()
                .nom("NoAuthorGame")
                .genre("RPG")
                .authorId(authorId)
                .build();

        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> gameService.create(dto));
        verify(authorRepository).findById(authorId);
        verify(gameRepository, never()).save(any());
    }

    @Test
    void shouldUpdateGameAndChangeAuthor() {
        Long id = 5L;
        Long oldAuthorId = 1L;
        Long newAuthorId = 3L;

        Author oldAuthor = Author.builder().id(oldAuthorId).build();
        Game existing = Game.builder()
                .id(id)
                .nom("AncienNom")
                .genre("OldGenre")
                .author(oldAuthor)
                .build();
        when(gameRepository.findById(id)).thenReturn(Optional.of(existing));

        Author newAuthor = Author.builder().id(newAuthorId).build();
        when(authorRepository.findById(newAuthorId)).thenReturn(Optional.of(newAuthor));

        Game saved = Game.builder()
                .id(id)
                .nom("NouveauNom")
                .genre("NewGenre")
                .author(newAuthor)
                .build();
        when(gameRepository.save(any(Game.class))).thenReturn(saved);

        GameDTO toUpdate = GameDTO.builder()
                .nom("NouveauNom")
                .genre("NewGenre")
                .authorId(newAuthorId)
                .build();

        GameDTO result = gameService.update(id, toUpdate);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("NouveauNom", result.getNom());
        assertEquals("NewGenre", result.getGenre());
        assertEquals(newAuthorId, result.getAuthorId());
        verify(gameRepository).findById(id);
        verify(authorRepository).findById(newAuthorId);
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void shouldThrowWhenGameNotFoundOnUpdate() {
        Long id = 99L;
        when(gameRepository.findById(id)).thenReturn(Optional.empty());
        GameDTO dto = GameDTO.builder().nom("X").genre("Y").build();
        assertThrows(RuntimeException.class, () -> gameService.update(id, dto));
        verify(gameRepository).findById(id);
        verify(gameRepository, never()).save(any());
    }

    @Test
    void shouldDeleteGameSuccess() {
        Long id = 7L;
        when(gameRepository.existsById(id)).thenReturn(true);

        gameService.delete(id);

        verify(gameRepository).existsById(id);
        verify(gameRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        Long id = 8L;
        when(gameRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> gameService.delete(id));
        verify(gameRepository).existsById(id);
        verify(gameRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldFindByIdSuccess() {
        Long id = 11L;
        Author author = Author.builder().id(4L).build();
        Game g = Game.builder().id(id).nom("FindMe").genre("Sim").author(author).build();
        when(gameRepository.findById(id)).thenReturn(Optional.of(g));

        GameDTO dto = gameService.findById(id);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("FindMe", dto.getNom());
        assertEquals("Sim", dto.getGenre());
        assertEquals(4L, dto.getAuthorId());
        verify(gameRepository).findById(id);
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        Long id = 12L;
        when(gameRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> gameService.findById(id));
        verify(gameRepository).findById(id);
    }

    @Test
    void shouldFindAllAndOtherQueries() {
        Author a1 = Author.builder().id(1L).build();
        Author a2 = Author.builder().id(2L).build();
        Game g1 = Game.builder().id(1L).nom("Alpha").genre("Action").author(a1).build();
        Game g2 = Game.builder().id(2L).nom("Beta").genre("Puzzle").author(a2).build();

        when(gameRepository.findAll()).thenReturn(List.of(g1, g2));
        when(gameRepository.findByNomContainingIgnoreCase("Al")).thenReturn(List.of(g1));
        when(gameRepository.findByGenre("Puzzle")).thenReturn(List.of(g2));
        when(gameRepository.findByAuthorId(2L)).thenReturn(List.of(g2));

        var all = gameService.findAll();
        assertEquals(2, all.size());
        assertEquals("Alpha", all.get(0).getNom());

        var byNom = gameService.findByNomContaining("Al");
        assertEquals(1, byNom.size());
        assertEquals("Alpha", byNom.get(0).getNom());

        var byGenre = gameService.findByGenre("Puzzle");
        assertEquals(1, byGenre.size());
        assertEquals("Beta", byGenre.get(0).getNom());

        var byAuthor = gameService.findByAuthorId(2L);
        assertEquals(1, byAuthor.size());
        assertEquals(2L, byAuthor.get(0).getId());

        verify(gameRepository).findAll();
        verify(gameRepository).findByNomContainingIgnoreCase("Al");
        verify(gameRepository).findByGenre("Puzzle");
        verify(gameRepository).findByAuthorId(2L);
    }
}
