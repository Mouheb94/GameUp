package com.gamesUP.gamesUP.test.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.gamesUP.gamesUP.dto.PurchaseLineDTO;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.entity.Purchase;
import com.gamesUP.gamesUP.entity.PurchaseLine;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.PurchaseLineRepository;
import com.gamesUP.gamesUP.repository.PurchaseRepository;
import com.gamesUP.gamesUP.service.PurchaseLineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseLineServiceTest {

    @Mock
    private PurchaseLineRepository purchaseLineRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private PurchaseLineService purchaseLineService;

    @Test
    void shouldCreateWithPurchaseSuccess() {
        Long gameId = 1L;
        Long purchaseId = 2L;
        PurchaseLineDTO dto = PurchaseLineDTO.builder()
                .gameId(gameId)
                .quantity(3)
                .price(19.99)
                .build();

        Game g = Game.builder().id(gameId).build();
        Purchase p = Purchase.builder().id(purchaseId).build();
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(g));
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(p));

        PurchaseLine saved = PurchaseLine.builder()
                .id(10L)
                .game(g)
                .purchase(p)
                .quantity(3)
                .price(19.99)
                .build();
        when(purchaseLineRepository.save(any(PurchaseLine.class))).thenReturn(saved);

        PurchaseLineDTO result = purchaseLineService.create(purchaseId, dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(gameId, result.getGameId());
        assertEquals(3, result.getQuantity());
        assertEquals(19.99, result.getPrice());
        verify(gameRepository).findById(gameId);
        verify(purchaseRepository).findById(purchaseId);
        verify(purchaseLineRepository).save(any(PurchaseLine.class));
    }

    @Test
    void shouldCreateWithoutPurchaseSuccess() {
        Long gameId = 4L;
        PurchaseLineDTO dto = PurchaseLineDTO.builder()
                .gameId(gameId)
                .quantity(1)
                .price(9.5)
                .build();

        Game g = Game.builder().id(gameId).build();
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(g));

        PurchaseLine saved = PurchaseLine.builder()
                .id(11L)
                .game(g)
                .quantity(1)
                .price(9.5)
                .build();
        when(purchaseLineRepository.save(any(PurchaseLine.class))).thenReturn(saved);

        PurchaseLineDTO result = purchaseLineService.create(null, dto);

        assertNotNull(result);
        assertEquals(11L, result.getId());
        assertEquals(gameId, result.getGameId());
        assertEquals(1, result.getQuantity());
        assertEquals(9.5, result.getPrice());
        verify(gameRepository).findById(gameId);
        verify(purchaseLineRepository).save(any(PurchaseLine.class));
        verify(purchaseRepository, never()).findById(anyLong());
    }

    @Test
    void shouldThrowWhenGameNotFoundOnCreate() {
        Long gameId = 99L;
        PurchaseLineDTO dto = PurchaseLineDTO.builder()
                .gameId(gameId)
                .quantity(2)
                .price(5.0)
                .build();

        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> purchaseLineService.create(null, dto));
        verify(gameRepository).findById(gameId);
        verify(purchaseLineRepository, never()).save(any());
    }

    @Test
    void shouldFindByIdSuccess() {
        Long id = 5L;
        Game g = Game.builder().id(7L).build();
        PurchaseLine pl = PurchaseLine.builder()
                .id(id)
                .game(g)
                .quantity(4)
                .price(12.0)
                .build();
        when(purchaseLineRepository.findById(id)).thenReturn(Optional.of(pl));

        PurchaseLineDTO dto = purchaseLineService.findById(id);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals(7L, dto.getGameId());
        assertEquals(4, dto.getQuantity());
        assertEquals(12.0, dto.getPrice());
        verify(purchaseLineRepository).findById(id);
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        Long id = 42L;
        when(purchaseLineRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> purchaseLineService.findById(id));
        verify(purchaseLineRepository).findById(id);
    }

    @Test
    void shouldUpdateChangeGameAndPurchase() {
        Long id = 6L;
        Long oldGameId = 1L;
        Long newGameId = 2L;
        Long newPurchaseId = 3L;

        Game oldGame = Game.builder().id(oldGameId).build();
        Purchase oldPurchase = Purchase.builder().id(9L).build();
        PurchaseLine existing = PurchaseLine.builder()
                .id(id)
                .game(oldGame)
                .purchase(oldPurchase)
                .quantity(2)
                .price(4.0)
                .build();
        when(purchaseLineRepository.findById(id)).thenReturn(Optional.of(existing));

        Game newGame = Game.builder().id(newGameId).build();
        when(gameRepository.findById(newGameId)).thenReturn(Optional.of(newGame));
        Purchase newPurchase = Purchase.builder().id(newPurchaseId).build();
        when(purchaseRepository.findById(newPurchaseId)).thenReturn(Optional.of(newPurchase));

        PurchaseLine saved = PurchaseLine.builder()
                .id(id)
                .game(newGame)
                .purchase(newPurchase)
                .quantity(5)
                .price(7.5)
                .build();
        when(purchaseLineRepository.save(any(PurchaseLine.class))).thenReturn(saved);

        PurchaseLineDTO toUpdate = PurchaseLineDTO.builder()
                .gameId(newGameId)
                .quantity(5)
                .price(7.5)
                .build();

        PurchaseLineDTO result = purchaseLineService.update(id, newPurchaseId, toUpdate);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(newGameId, result.getGameId());
        assertEquals(5, result.getQuantity());
        assertEquals(7.5, result.getPrice());
        verify(purchaseLineRepository).findById(id);
        verify(gameRepository).findById(newGameId);
        verify(purchaseRepository).findById(newPurchaseId);
        verify(purchaseLineRepository).save(any(PurchaseLine.class));
    }

    @Test
    void shouldThrowWhenUpdateNotFound() {
        Long id = 100L;
        when(purchaseLineRepository.findById(id)).thenReturn(Optional.empty());
        PurchaseLineDTO dto = PurchaseLineDTO.builder().gameId(1L).quantity(1).price(1.0).build();
        assertThrows(RuntimeException.class, () -> purchaseLineService.update(id, null, dto));
        verify(purchaseLineRepository).findById(id);
        verify(purchaseLineRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUpdateGameNotFound() {
        Long id = 12L;
        PurchaseLine existing = PurchaseLine.builder().id(id).build();
        when(purchaseLineRepository.findById(id)).thenReturn(Optional.of(existing));
        Long missingGameId = 77L;
        when(gameRepository.findById(missingGameId)).thenReturn(Optional.empty());

        PurchaseLineDTO dto = PurchaseLineDTO.builder()
                .gameId(missingGameId)
                .quantity(1)
                .price(2.0)
                .build();

        assertThrows(RuntimeException.class, () -> purchaseLineService.update(id, null, dto));
        verify(purchaseLineRepository).findById(id);
        verify(gameRepository).findById(missingGameId);
        verify(purchaseLineRepository, never()).save(any());
    }

    @Test
    void shouldDeleteSuccess() {
        Long id = 8L;
        when(purchaseLineRepository.existsById(id)).thenReturn(true);

        purchaseLineService.delete(id);

        verify(purchaseLineRepository).existsById(id);
        verify(purchaseLineRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        Long id = 9L;
        when(purchaseLineRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> purchaseLineService.delete(id));
        verify(purchaseLineRepository).existsById(id);
        verify(purchaseLineRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldFindAll() {
        Game g1 = Game.builder().id(1L).build();
        Game g2 = Game.builder().id(2L).build();
        PurchaseLine pl1 = PurchaseLine.builder().id(1L).game(g1).quantity(2).price(3.0).build();
        PurchaseLine pl2 = PurchaseLine.builder().id(2L).game(g2).quantity(4).price(6.0).build();

        when(purchaseLineRepository.findAll()).thenReturn(List.of(pl1, pl2));

        List<PurchaseLineDTO> list = purchaseLineService.findAll();

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(1L, list.get(0).getGameId());
        assertEquals(2L, list.get(1).getId());
        assertEquals(2L, list.get(1).getGameId());
        verify(purchaseLineRepository).findAll();
    }
}
