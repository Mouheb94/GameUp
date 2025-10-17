package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.PurchaseLineDTO;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.entity.Purchase;
import com.gamesUP.gamesUP.entity.PurchaseLine;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.PurchaseLineRepository;
import com.gamesUP.gamesUP.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseLineService {

    private final PurchaseLineRepository purchaseLineRepository;
    private final GameRepository gameRepository;
    private final PurchaseRepository purchaseRepository;

    public PurchaseLineDTO create(Long purchaseId, PurchaseLineDTO dto) {
        PurchaseLine pl = toEntity(dto);
        if (purchaseId != null) {
            Purchase p = purchaseRepository.findById(purchaseId)
                    .orElseThrow(() -> new RuntimeException("Purchase not found: " + purchaseId));
            pl.setPurchase(p);
        }
        PurchaseLine saved = purchaseLineRepository.save(pl);
        return toDto(saved);
    }

    public PurchaseLineDTO update(Long id, Long purchaseId, PurchaseLineDTO dto) {
        PurchaseLine existing = purchaseLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PurchaseLine not found: " + id));
        if (dto.getGameId() != null && (existing.getGame() == null || !existing.getGame().getId().equals(dto.getGameId()))) {
            Game g = gameRepository.findById(dto.getGameId())
                    .orElseThrow(() -> new RuntimeException("Game not found: " + dto.getGameId()));
            existing.setGame(g);
        }
        existing.setQuantity(dto.getQuantity());
        existing.setPrice(dto.getPrice());
        if (purchaseId != null) {
            Purchase p = purchaseRepository.findById(purchaseId)
                    .orElseThrow(() -> new RuntimeException("Purchase not found: " + purchaseId));
            existing.setPurchase(p);
        }
        PurchaseLine saved = purchaseLineRepository.save(existing);
        return toDto(saved);
    }

    public void delete(Long id) {
        if (!purchaseLineRepository.existsById(id)) {
            throw new RuntimeException("PurchaseLine not found: " + id);
        }
        purchaseLineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PurchaseLineDTO findById(Long id) {
        PurchaseLine pl = purchaseLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PurchaseLine not found: " + id));
        return toDto(pl);
    }

    @Transactional(readOnly = true)
    public List<PurchaseLineDTO> findAll() {
        return purchaseLineRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private PurchaseLine toEntity(PurchaseLineDTO dto) {
        PurchaseLine pl = new PurchaseLine();
        pl.setId(dto.getId());
        if (dto.getGameId() != null) {
            Game g = gameRepository.findById(dto.getGameId())
                    .orElseThrow(() -> new RuntimeException("Game not found: " + dto.getGameId()));
            pl.setGame(g);
        }
        pl.setQuantity(dto.getQuantity());
        pl.setPrice(dto.getPrice());
        return pl;
    }

    private PurchaseLineDTO toDto(PurchaseLine pl) {
        PurchaseLineDTO dto = PurchaseLineDTO.builder()
                .id(pl.getId())
                .gameId(pl.getGame() != null ? pl.getGame().getId() : null)
                .quantity(pl.getQuantity())
                .price(pl.getPrice())
                .build();
        return dto;
    }
}
