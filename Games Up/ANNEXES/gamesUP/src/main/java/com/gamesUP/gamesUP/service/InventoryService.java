package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.InventoryDTO;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.entity.Inventory;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final GameRepository gameRepository;

    @Transactional
    public InventoryDTO create(InventoryDTO dto) {
        Inventory inv = toEntity(dto);
        Inventory saved = inventoryRepository.save(inv);
        return toDto(saved);
    }

    @Transactional
    public InventoryDTO update(Long id, InventoryDTO dto) {
        Inventory existing = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found: " + id));
        // replace stock
        existing.setStock(toEntity(dto).getStock());
        Inventory saved = inventoryRepository.save(existing);
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new RuntimeException("Inventory not found: " + id);
        }
        inventoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public InventoryDTO findById(Long id) {
        Inventory inv = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found: " + id));
        return toDto(inv);
    }

    @Transactional(readOnly = true)
    public List<InventoryDTO> findAll() {
        return inventoryRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Inventory toEntity(InventoryDTO dto) {
        Inventory inv = new Inventory();
        inv.setId(dto.getId());
        Map<Game, Integer> stock = new HashMap<>();
        if (dto.getStock() != null) {
            dto.getStock().forEach((gameId, qty) -> {
                Game game = gameRepository.findById(gameId)
                        .orElseThrow(() -> new RuntimeException("Game not found: " + gameId));
                stock.put(game, qty);
            });
        }
        inv.setStock(stock);
        return inv;
    }

    private InventoryDTO toDto(Inventory inv) {
        InventoryDTO dto = new InventoryDTO();
        dto.setId(inv.getId());
        Map<Long, Integer> stockDto = new HashMap<>();
        if (inv.getStock() != null) {
            inv.getStock().forEach((game, qty) -> {
                if (game != null && game.getId() != null) {
                    stockDto.put(game.getId(), qty);
                }
            });
        }
        dto.setStock(stockDto);
        return dto;
    }
}