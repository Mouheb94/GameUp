package com.gamesUP.gamesUP.test.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import com.gamesUP.gamesUP.dto.PurchaseDTO;
import com.gamesUP.gamesUP.dto.PurchaseLineDTO;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.entity.Purchase;
import com.gamesUP.gamesUP.entity.PurchaseLine;
import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.PurchaseLineRepository;
import com.gamesUP.gamesUP.repository.PurchaseRepository;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.service.PurchaseService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private PurchaseLineRepository purchaseLineRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    void shouldCreatePurchaseSuccess() {
        Long userId = 1L;
        Long gameId = 2L;

        PurchaseLineDTO lineDto = PurchaseLineDTO.builder()
                .gameId(gameId)
                .quantity(3)
                .build();
        PurchaseDTO dto = PurchaseDTO.builder()
                .userId(userId)
                .lines(List.of(lineDto))
                .paid(true)
                .delivered(false)
                .archived(false)
                .build();

        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Game game = Game.builder().id(gameId).build();
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        PurchaseLine savedLine = PurchaseLine.builder().id(100L).game(game).quantity(3).build();
        Purchase saved = Purchase.builder()
                .id(10L)
                .user(user)
                .date(new Date())
                .paid(true)
                .delivered(false)
                .archived(false)
                .lines(List.of(savedLine))
                .build();
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(saved);

        PurchaseDTO result = purchaseService.create(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(1, result.getLines().size());
        assertEquals(gameId, result.getLines().get(0).getGameId());
        verify(userRepository).findById(userId);
        verify(gameRepository).findById(gameId);
        verify(purchaseRepository).save(any(Purchase.class));
    }

    @Test
    void shouldThrowWhenUserNotFoundOnCreate() {
        Long userId = 99L;
        PurchaseDTO dto = PurchaseDTO.builder()
                .userId(userId)
                .lines(List.of())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> purchaseService.create(dto));
        verify(userRepository).findById(userId);
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenGameNotFoundOnCreate() {
        Long userId = 1L;
        Long missingGameId = 77L;
        PurchaseLineDTO lineDto = PurchaseLineDTO.builder().gameId(missingGameId).quantity(1).build();
        PurchaseDTO dto = PurchaseDTO.builder()
                .userId(userId)
                .lines(List.of(lineDto))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(gameRepository.findById(missingGameId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> purchaseService.create(dto));
        verify(userRepository).findById(userId);
        verify(gameRepository).findById(missingGameId);
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void shouldUpdatePurchaseSuccess() {
        Long id = 5L;
        Long oldGameId = 1L;
        Long newGameId = 2L;
        Long newUserId = 3L;

        Game oldGame = Game.builder().id(oldGameId).build();
        PurchaseLine oldLine = PurchaseLine.builder().id(11L).game(oldGame).quantity(1).build();
        Purchase existing = Purchase.builder()
                .id(id)
                .user(User.builder().id(10L).build())
                .date(new Date())
                .paid(false)
                .delivered(false)
                .archived(false)
                .lines(new ArrayList<>(List.of(oldLine)))
                .build();
        // ensure existing.getLines() not null
        oldLine.setPurchase(existing);

        when(purchaseRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.findById(newUserId)).thenReturn(Optional.of(User.builder().id(newUserId).build()));
        when(gameRepository.findById(newGameId)).thenReturn(Optional.of(Game.builder().id(newGameId).build()));

        PurchaseLine savedLine = PurchaseLine.builder().id(200L).game(Game.builder().id(newGameId).build()).quantity(5).build();
        Purchase saved = Purchase.builder()
                .id(id)
                .user(User.builder().id(newUserId).build())
                .lines(List.of(savedLine))
                .paid(true)
                .delivered(true)
                .archived(false)
                .date(new Date())
                .build();
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(saved);

        PurchaseLineDTO newLineDto = PurchaseLineDTO.builder().gameId(newGameId).quantity(5).build();
        PurchaseDTO toUpdate = PurchaseDTO.builder()
                .userId(newUserId)
                .lines(List.of(newLineDto))
                .paid(true)
                .delivered(true)
                .archived(false)
                .build();

        PurchaseDTO result = purchaseService.update(id, toUpdate);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(newUserId, result.getUserId());
        assertEquals(1, result.getLines().size());
        assertEquals(newGameId, result.getLines().get(0).getGameId());
        verify(purchaseRepository).findById(id);
        verify(userRepository).findById(newUserId);
        verify(gameRepository).findById(newGameId);
        verify(purchaseRepository).save(any(Purchase.class));
    }

    @Test
    void shouldThrowWhenUpdatePurchaseNotFound() {
        Long id = 99L;
        when(purchaseRepository.findById(id)).thenReturn(Optional.empty());
        PurchaseDTO dto = PurchaseDTO.builder().lines(List.of()).build();

        assertThrows(RuntimeException.class, () -> purchaseService.update(id, dto));
        verify(purchaseRepository).findById(id);
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUserNotFoundOnUpdate() {
        Long id = 6L;
        Purchase existing = Purchase.builder()
                .id(id)
                .lines(new ArrayList<>())
                .build();
        when(purchaseRepository.findById(id)).thenReturn(Optional.of(existing));
        Long missingUserId = 42L;
        PurchaseDTO dto = PurchaseDTO.builder()
                .userId(missingUserId)
                .lines(List.of())
                .build();
        when(userRepository.findById(missingUserId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> purchaseService.update(id, dto));
        verify(purchaseRepository).findById(id);
        verify(userRepository).findById(missingUserId);
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenGameNotFoundOnUpdateLines() {
        Long id = 7L;
        Purchase existing = Purchase.builder()
                .id(id)
                .lines(new ArrayList<>())
                .build();
        when(purchaseRepository.findById(id)).thenReturn(Optional.of(existing));

        Long missingGameId = 88L;
        PurchaseLineDTO lineDto = PurchaseLineDTO.builder().gameId(missingGameId).quantity(1).build();
        PurchaseDTO dto = PurchaseDTO.builder().lines(List.of(lineDto)).build();

        when(gameRepository.findById(missingGameId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> purchaseService.update(id, dto));
        verify(purchaseRepository).findById(id);
        verify(gameRepository).findById(missingGameId);
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void shouldDeletePurchaseSuccess() {
        Long id = 12L;
        when(purchaseRepository.existsById(id)).thenReturn(true);

        purchaseService.delete(id);

        verify(purchaseRepository).existsById(id);
        verify(purchaseRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        Long id = 13L;
        when(purchaseRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> purchaseService.delete(id));
        verify(purchaseRepository).existsById(id);
        verify(purchaseRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldFindByIdSuccess() {
        Long id = 20L;
        User user = User.builder().id(5L).build();
        Game g = Game.builder().id(9L).build();
        PurchaseLine pl = PurchaseLine.builder().id(33L).game(g).quantity(2).build();
        Purchase p = Purchase.builder()
                .id(id)
                .user(user)
                .lines(List.of(pl))
                .paid(false)
                .delivered(false)
                .archived(false)
                .date(new Date())
                .build();
        when(purchaseRepository.findById(id)).thenReturn(Optional.of(p));

        PurchaseDTO dto = purchaseService.findById(id);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals(5L, dto.getUserId());
        assertEquals(1, dto.getLines().size());
        assertEquals(9L, dto.getLines().get(0).getGameId());
        verify(purchaseRepository).findById(id);
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        Long id = 999L;
        when(purchaseRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> purchaseService.findById(id));
        verify(purchaseRepository).findById(id);
    }

    @Test
    void shouldFindAll() {
        User u1 = User.builder().id(1L).build();
        User u2 = User.builder().id(2L).build();
        Purchase p1 = Purchase.builder().id(1L).user(u1).lines(List.of()).build();
        Purchase p2 = Purchase.builder().id(2L).user(u2).lines(List.of()).build();

        when(purchaseRepository.findAll()).thenReturn(List.of(p1, p2));

        List<PurchaseDTO> list = purchaseService.findAll();

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(1L, list.get(0).getUserId());
        assertEquals(2L, list.get(1).getId());
        assertEquals(2L, list.get(1).getUserId());
        verify(purchaseRepository).findAll();
    }
}
