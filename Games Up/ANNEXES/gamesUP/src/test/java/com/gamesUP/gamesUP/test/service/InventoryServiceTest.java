
package com.gamesUP.gamesUP.test.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;
import com.gamesUP.gamesUP.dto.InventoryDTO;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.entity.Inventory;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.InventoryRepository;
import com.gamesUP.gamesUP.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void shouldCreateInventorySuccess() {
        Long g1Id = 1L;
        InventoryDTO dto = InventoryDTO.builder()
                .stock(Map.of(g1Id, 5))
                .build();

        Game g1 = Game.builder().id(g1Id).build();
        when(gameRepository.findById(g1Id)).thenReturn(Optional.of(g1));

        Inventory saved = Inventory.builder()
                .id(10L)
                .stock(Map.of(g1, 5))
                .build();
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(saved);

        InventoryDTO result = inventoryService.create(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(1, result.getStock().size());
        assertEquals(5, result.getStock().get(g1Id));
        verify(gameRepository).findById(g1Id);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void shouldCreate_whenStockNull_createsEmptyStock() {
        InventoryDTO dto = InventoryDTO.builder().stock(null).build();

        Inventory saved = Inventory.builder().id(20L).stock(Collections.emptyMap()).build();
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(saved);

        InventoryDTO result = inventoryService.create(dto);

        assertNotNull(result);
        assertEquals(20L, result.getId());
        assertNotNull(result.getStock());
        assertTrue(result.getStock().isEmpty());
        verify(inventoryRepository).save(any(Inventory.class));
        verifyNoInteractions(gameRepository);
    }

    @Test
    void shouldThrowWhenGameNotFoundOnCreate() {
        Long gId = 99L;
        InventoryDTO dto = InventoryDTO.builder()
                .stock(Map.of(gId, 1))
                .build();

        when(gameRepository.findById(gId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> inventoryService.create(dto));
        verify(gameRepository).findById(gId);
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void shouldFindByIdSuccess_andHandleNullGameEntries() {
        Long id = 5L;
        Map<Game, Integer> stockWithNullKey = new java.util.HashMap<>();
        stockWithNullKey.put(null, 7);

        Inventory inv = Inventory.builder()
                .id(id)
                .stock(stockWithNullKey)
                .build();
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(inv));

        InventoryDTO dto = inventoryService.findById(id);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertTrue(dto.getStock().isEmpty());
        verify(inventoryRepository).findById(id);
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        Long id = 42L;
        when(inventoryRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> inventoryService.findById(id));
        verify(inventoryRepository).findById(id);
    }

    @Test
    void shouldUpdateInventorySuccess() {
        Long id = 8L;
        Game oldGame = Game.builder().id(1L).build();
        Inventory existing = Inventory.builder().id(id).stock(Map.of(oldGame, 2)).build();
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(existing));

        Long newGameId = 3L;
        InventoryDTO toUpdate = InventoryDTO.builder().stock(Map.of(newGameId, 9)).build();

        Game newGame = Game.builder().id(newGameId).build();
        when(gameRepository.findById(newGameId)).thenReturn(Optional.of(newGame));

        Inventory saved = Inventory.builder().id(id).stock(Map.of(newGame, 9)).build();
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(saved);

        InventoryDTO result = inventoryService.update(id, toUpdate);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(1, result.getStock().size());
        assertEquals(9, result.getStock().get(newGameId));
        verify(inventoryRepository).findById(id);
        verify(gameRepository).findById(newGameId);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void shouldDeleteInventorySuccess() {
        Long id = 7L;
        when(inventoryRepository.existsById(id)).thenReturn(true);

        inventoryService.delete(id);

        verify(inventoryRepository).existsById(id);
        verify(inventoryRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        Long id = 11L;
        when(inventoryRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> inventoryService.delete(id));
        verify(inventoryRepository).existsById(id);
        verify(inventoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldFindAll() {
        Game g1 = Game.builder().id(1L).build();
        Inventory i1 = Inventory.builder().id(1L).stock(Map.of(g1, 4)).build();

        when(inventoryRepository.findAll()).thenReturn(List.of(i1));

        List<InventoryDTO> list = inventoryService.findAll();

        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(4, list.get(0).getStock().get(1L));
        verify(inventoryRepository).findAll();
    }
}
