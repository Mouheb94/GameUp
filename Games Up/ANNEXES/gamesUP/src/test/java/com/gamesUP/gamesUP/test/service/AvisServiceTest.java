
package com.gamesUP.gamesUP.test.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.gamesUP.gamesUP.dto.AvisDTO;
import com.gamesUP.gamesUP.entity.Avis;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.repository.AvisRepository;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.service.AvisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AvisServiceTest {

    @Mock
    private AvisRepository avisRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private AvisService avisService;

    @Test
    void shouldCreateAvisSuccess() {
        Long gameId = 10L;
        AvisDTO dto = AvisDTO.builder()
                .commentaire("Très bon jeu")
                .note(9)
                .gameId(gameId)
                .build();

        Game gameMock = mock(Game.class);
        when(gameMock.getId()).thenReturn(gameId);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(gameMock));

        Avis saved = Avis.builder()
                .id(1L)
                .commentaire(dto.getCommentaire())
                .note(dto.getNote())
                .game(gameMock)
                .build();
        when(avisRepository.save(any(Avis.class))).thenReturn(saved);

        AvisDTO result = avisService.create(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(dto.getCommentaire(), result.getCommentaire());
        assertEquals(dto.getNote(), result.getNote());
        assertEquals(gameId, result.getGameId());
        verify(gameRepository).findById(gameId);
        verify(avisRepository).save(any(Avis.class));
    }

    @Test
    void shouldThrowWhenGameNotFoundOnCreate() {
        Long gameId = 20L;
        AvisDTO dto = AvisDTO.builder()
                .commentaire("Test")
                .note(5)
                .gameId(gameId)
                .build();

        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> avisService.create(dto));
        verify(gameRepository).findById(gameId);
        verify(avisRepository, never()).save(any());
    }

    @Test
    void shouldFindByIdSuccess() {
        Long id = 2L;
        Game gameMock = mock(Game.class);
        when(gameMock.getId()).thenReturn(7L);

        Avis avis = Avis.builder()
                .id(id)
                .commentaire("Comment")
                .note(6)
                .game(gameMock)
                .build();
        when(avisRepository.findById(id)).thenReturn(Optional.of(avis));

        AvisDTO dto = avisService.findById(id);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Comment", dto.getCommentaire());
        assertEquals(6, dto.getNote());
        assertEquals(7L, dto.getGameId());
        verify(avisRepository).findById(id);
    }

    @Test
    void shouldFindByIdWhenGameNull_returnsNullGameId() {
        Long id = 21L;
        Avis avis = Avis.builder()
                .id(id)
                .commentaire("NoGame")
                .note(3)
                .game(null)
                .build();
        when(avisRepository.findById(id)).thenReturn(Optional.of(avis));

        AvisDTO dto = avisService.findById(id);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("NoGame", dto.getCommentaire());
        assertNull(dto.getGameId());
        verify(avisRepository).findById(id);
    }

    @Test
    void shouldThrowWhenAvisNotFoundOnFind() {
        Long id = 99L;
        when(avisRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> avisService.findById(id));
        verify(avisRepository).findById(id);
    }

    @Test
    void shouldUpdateAvisAndChangeGame() {
        Long id = 3L;
        Long oldGameId = 1L;
        Long newGameId = 2L;

        Game oldGame = mock(Game.class);
        when(oldGame.getId()).thenReturn(oldGameId);
        Avis existing = Avis.builder()
                .id(id)
                .commentaire("Ancien")
                .note(4)
                .game(oldGame)
                .build();
        when(avisRepository.findById(id)).thenReturn(Optional.of(existing));

        Game newGame = mock(Game.class);
        when(newGame.getId()).thenReturn(newGameId);
        when(gameRepository.findById(newGameId)).thenReturn(Optional.of(newGame));

        Avis saved = Avis.builder()
                .id(id)
                .commentaire("Nouveau")
                .note(8)
                .game(newGame)
                .build();
        when(avisRepository.save(any(Avis.class))).thenReturn(saved);

        AvisDTO toUpdate = AvisDTO.builder()
                .commentaire("Nouveau")
                .note(8)
                .gameId(newGameId)
                .build();

        AvisDTO result = avisService.update(id, toUpdate);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Nouveau", result.getCommentaire());
        assertEquals(8, result.getNote());
        assertEquals(newGameId, result.getGameId());
        verify(avisRepository).findById(id);
        verify(gameRepository).findById(newGameId);
        verify(avisRepository).save(any(Avis.class));
    }

    @Test
    void shouldUpdateAvis_whenGameIdNull_keepExistingGame() {
        Long id = 11L;
        Game existingGame = mock(Game.class);
        when(existingGame.getId()).thenReturn(55L);
        Avis existing = Avis.builder()
                .id(id)
                .commentaire("Keep")
                .note(2)
                .game(existingGame)
                .build();
        when(avisRepository.findById(id)).thenReturn(Optional.of(existing));

        Avis saved = Avis.builder()
                .id(id)
                .commentaire("KeepUpdated")
                .note(3)
                .game(existingGame)
                .build();
        when(avisRepository.save(any(Avis.class))).thenReturn(saved);

        AvisDTO toUpdate = AvisDTO.builder()
                .commentaire("KeepUpdated")
                .note(3)
                .gameId(null) // explicit null -> should not lookup gameRepository
                .build();

        AvisDTO result = avisService.update(id, toUpdate);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("KeepUpdated", result.getCommentaire());
        assertEquals(3, result.getNote());
        assertEquals(55L, result.getGameId());
        verify(avisRepository).findById(id);
        verify(gameRepository, never()).findById(anyLong());
        verify(avisRepository).save(any(Avis.class));
    }

    @Test
    void shouldUpdateAvis_whenGameIdEqualsExisting_noLookup() {
        Long id = 12L;
        Long sameGameId = 77L;
        Game existingGame = mock(Game.class);
        when(existingGame.getId()).thenReturn(sameGameId);
        Avis existing = Avis.builder()
                .id(id)
                .commentaire("Same")
                .note(1)
                .game(existingGame)
                .build();
        when(avisRepository.findById(id)).thenReturn(Optional.of(existing));

        Avis saved = Avis.builder()
                .id(id)
                .commentaire("SameUpdated")
                .note(2)
                .game(existingGame)
                .build();
        when(avisRepository.save(any(Avis.class))).thenReturn(saved);

        AvisDTO toUpdate = AvisDTO.builder()
                .commentaire("SameUpdated")
                .note(2)
                .gameId(sameGameId) // equals existing -> should not lookup
                .build();

        AvisDTO result = avisService.update(id, toUpdate);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("SameUpdated", result.getCommentaire());
        assertEquals(2, result.getNote());
        assertEquals(sameGameId, result.getGameId());
        verify(avisRepository).findById(id);
        verify(gameRepository, never()).findById(anyLong());
        verify(avisRepository).save(any(Avis.class));
    }

    @Test
    void shouldDeleteAvisSuccess() {
        Long id = 5L;
        when(avisRepository.existsById(id)).thenReturn(true);

        avisService.delete(id);

        verify(avisRepository).existsById(id);
        verify(avisRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        Long id = 6L;
        when(avisRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> avisService.delete(id));
        verify(avisRepository).existsById(id);
        verify(avisRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldFindAll() {
        Game g1 = mock(Game.class);
        when(g1.getId()).thenReturn(1L);
        Game g2 = mock(Game.class);
        when(g2.getId()).thenReturn(2L);

        Avis a1 = Avis.builder().id(1L).commentaire("A").note(5).game(g1).build();
        Avis a2 = Avis.builder().id(2L).commentaire("B").note(7).game(g2).build();

        when(avisRepository.findAll()).thenReturn(List.of(a1, a2));

        List<AvisDTO> list = avisService.findAll();

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(2L, list.get(1).getId());
        verify(avisRepository).findAll();
    }
}
