package com.gamesUP.gamesUP.testUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        Long g2Id = 2L;
        InventoryDTO dto = InventoryDTO.builder()
                .stock(Map.of(g1Id, 5, g2Id, 3))
                .build();

        Game g1 = Game.builder().id(g1Id).build();
        Game g2 = Game.builder().id(g2Id).build();
        when(gameRepository.findById(g1Id)).thenReturn(Optional.of(g1));
        when(gameRepository.findById(g2Id)).thenReturn(Optional.of(g2));

        Inventory saved = Inventory.builder()
                .id(10L)
                .stock(Map.of(g1, 5, g2, 3))
                .build();
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(saved);

        InventoryDTO result = inventoryService.create(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(2, result.getStock().size());
        assertEquals(5, result.getStock().get(g1Id));
        assertEquals(3, result.getStock().get(g2Id));
        verify(gameRepository).findById(g1Id);
        verify(gameRepository).findById(g2Id);
        verify(inventoryRepository).save(any(Inventory.class));
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
    void shouldFindByIdSuccess() {
        Long id = 5L;
        Game g = Game.builder().id(2L).build();
        Inventory inv = Inventory.builder()
                .id(id)
                .stock(Map.of(g, 7))
                .build();
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(inv));

        InventoryDTO dto = inventoryService.findById(id);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals(1, dto.getStock().size());
        assertEquals(7, dto.getStock().get(2L));
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
        Inventory existing = Inventory.builder()
                .id(id)
                .stock(Map.of(oldGame, 2))
                .build();
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(existing));

        Long newGameId = 3L;
        InventoryDTO toUpdate = InventoryDTO.builder()
                .stock(Map.of(newGameId, 9))
                .build();

        Game newGame = Game.builder().id(newGameId).build();
        when(gameRepository.findById(newGameId)).thenReturn(Optional.of(newGame));

        Inventory saved = Inventory.builder()
                .id(id)
                .stock(Map.of(newGame, 9))
                .build();
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
        Game g2 = Game.builder().id(2L).build();
        Inventory i1 = Inventory.builder().id(1L).stock(Map.of(g1, 4)).build();
        Inventory i2 = Inventory.builder().id(2L).stock(Map.of(g2, 6)).build();

        when(inventoryRepository.findAll()).thenReturn(List.of(i1, i2));

        List<InventoryDTO> list = inventoryService.findAll();

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(4, list.get(0).getStock().get(1L));
        assertEquals(2L, list.get(1).getId());
        assertEquals(6, list.get(1).getStock().get(2L));
        verify(inventoryRepository).findAll();
    }
}
