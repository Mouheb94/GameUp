package com.gamesUP.gamesUP.test.service;

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
    void shouldCreateGameWithoutAuthor() {
        GameDTO dto = GameDTO.builder()
                .nom("SoloGame")
                .genre("Indie")
                .authorId(null)
                .build();

        Game saved = Game.builder()
                .id(11L)
                .nom(dto.getNom())
                .genre(dto.getGenre())
                .author(null)
                .build();
        when(gameRepository.save(any(Game.class))).thenReturn(saved);

        GameDTO result = gameService.create(dto);

        assertNotNull(result);
        assertEquals(11L, result.getId());
        assertNull(result.getAuthorId());
        verify(authorRepository, never()).findById(anyLong());
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void shouldThrowWhenAuthorNotFoundOnCreate() {
        Long authorId = 99L;
        GameDTO dto = GameDTO.builder().nom("Bad").authorId(authorId).build();

        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> gameService.create(dto));
        assertTrue(ex.getMessage().contains(String.valueOf(authorId)));
        verify(authorRepository).findById(authorId);
        verify(gameRepository, never()).save(any());
    }

    @Test
    void shouldUpdateGameAndChangeAuthor() {
        Long gameId = 2L;
        Author oldAuthor = Author.builder().id(1L).build();
        Game existing = Game.builder().id(gameId).nom("Old").genre("RPG").author(oldAuthor).build();
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(existing));

        Long newAuthorId = 2L;
        GameDTO dto = GameDTO.builder().nom("New").genre("Adventure").authorId(newAuthorId).build();

        Author newAuthor = Author.builder().id(newAuthorId).build();
        when(authorRepository.findById(newAuthorId)).thenReturn(Optional.of(newAuthor));

        Game saved = Game.builder().id(gameId).nom("New").genre("Adventure").author(newAuthor).build();
        when(gameRepository.save(existing)).thenReturn(saved);

        GameDTO result = gameService.update(gameId, dto);

        assertEquals(gameId, result.getId());
        assertEquals("New", result.getNom());
        assertEquals("Adventure", result.getGenre());
        assertEquals(newAuthorId, result.getAuthorId());
        verify(gameRepository).findById(gameId);
        verify(authorRepository).findById(newAuthorId);
        verify(gameRepository).save(existing);
    }

    @Test
    void shouldUpdateGameWhenAuthorUnchanged() {
        Long gameId = 3L;
        Author author = Author.builder().id(5L).build();
        Game existing = Game.builder().id(gameId).nom("Keep").genre("Sim").author(author).build();
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(existing));

        GameDTO dto = GameDTO.builder().nom("Keep2").genre("Sim2").authorId(5L).build();

        Game saved = Game.builder().id(gameId).nom("Keep2").genre("Sim2").author(author).build();
        when(gameRepository.save(existing)).thenReturn(saved);

        GameDTO result = gameService.update(gameId, dto);

        assertEquals(5L, result.getAuthorId());
        verify(authorRepository, never()).findById(anyLong());
        verify(gameRepository).save(existing);
    }

    @Test
    void shouldUpdateGameAndSetAuthorWhenExistingAuthorNull() {
        Long gameId = 4L;
        Game existing = Game.builder().id(gameId).nom("NoAuth").genre("Misc").author(null).build();
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(existing));

        Long newAuthorId = 7L;
        GameDTO dto = GameDTO.builder().nom("WithAuth").genre("Misc2").authorId(newAuthorId).build();

        Author newAuthor = Author.builder().id(newAuthorId).build();
        when(authorRepository.findById(newAuthorId)).thenReturn(Optional.of(newAuthor));

        Game saved = Game.builder().id(gameId).nom("WithAuth").genre("Misc2").author(newAuthor).build();
        when(gameRepository.save(existing)).thenReturn(saved);

        GameDTO result = gameService.update(gameId, dto);

        assertEquals(newAuthorId, result.getAuthorId());
        verify(authorRepository).findById(newAuthorId);
        verify(gameRepository).save(existing);
    }

    @Test
    void shouldThrowWhenGameNotFoundOnUpdate() {
        Long id = 50L;
        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> gameService.update(id, GameDTO.builder().build()));
        assertTrue(ex.getMessage().contains(String.valueOf(id)));
        verify(gameRepository).findById(id);
        verify(gameRepository, never()).save(any());
    }

    @Test
    void shouldDeleteGameSuccess() {
        Long id = 20L;
        when(gameRepository.existsById(id)).thenReturn(true);

        gameService.delete(id);

        verify(gameRepository).existsById(id);
        verify(gameRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        Long id = 21L;
        when(gameRepository.existsById(id)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> gameService.delete(id));
        assertTrue(ex.getMessage().contains(String.valueOf(id)));
        verify(gameRepository).existsById(id);
        verify(gameRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldFindByIdSuccess() {
        Long id = 30L;
        Game g = Game.builder().id(id).nom("Found").genre("Arcade").build();
        when(gameRepository.findById(id)).thenReturn(Optional.of(g));

        GameDTO dto = gameService.findById(id);

        assertEquals(id, dto.getId());
        assertEquals("Found", dto.getNom());
        verify(gameRepository).findById(id);
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        Long id = 31L;
        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> gameService.findById(id));
        assertTrue(ex.getMessage().contains(String.valueOf(id)));
        verify(gameRepository).findById(id);
    }

    @Test
    void shouldFindAllAndOtherQueries() {
        Game g1 = Game.builder().id(1L).nom("Alpha").genre("Puzzle").build();
        Game g2 = Game.builder().id(2L).nom("Beta").genre("Puzzle").build();
        when(gameRepository.findAll()).thenReturn(List.of(g1, g2));
        when(gameRepository.findByNomContainingIgnoreCase("Al")).thenReturn(List.of(g1));
        when(gameRepository.findByGenre("Puzzle")).thenReturn(List.of(g2));
        when(gameRepository.findByAuthorId(2L)).thenReturn(List.of(Game.builder().id(2L).nom("ByAuth").build()));

        var all = gameService.findAll();
        assertEquals(2, all.size());

        var byName = gameService.findByNomContaining("Al");
        assertEquals(1, byName.size());
        assertEquals("Alpha", byName.get(0).getNom());

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
