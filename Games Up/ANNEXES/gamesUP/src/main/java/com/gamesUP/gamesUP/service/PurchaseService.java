package com.gamesUP.gamesUP.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseLineRepository purchaseLineRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @Transactional
    public PurchaseDTO create(PurchaseDTO dto) {
        Purchase p = toEntity(dto);
        Purchase saved = purchaseRepository.save(p);
        // cascade should save lines, but ensure IDs are refreshed
        return toDto(saved);
    }

    @Transactional
    public PurchaseDTO update(Long id, PurchaseDTO dto) {
        Purchase existing = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found: " + id));
        // replace basic fields
        existing.setDate(dto.getDate() != null ? dto.getDate() : existing.getDate());
        existing.setPaid(dto.isPaid());
        existing.setDelivered(dto.isDelivered());
        existing.setArchived(dto.isArchived());
        // replace user if provided
        if (dto.getUserId() != null) {
            User u = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserId()));
            existing.setUser(u);
        }
        // replace lines: remove existing and set new ones
        existing.getLines().clear();
        List<PurchaseLine> newLines = dto.getLines().stream()
                .map(l -> toEntityLine(l, existing))
                .collect(Collectors.toList());
        existing.setLines(newLines);
        Purchase saved = purchaseRepository.save(existing);
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!purchaseRepository.existsById(id)) {
            throw new RuntimeException("Purchase not found: " + id);
        }
        purchaseRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PurchaseDTO findById(Long id) {
        Purchase p = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found: " + id));
        return toDto(p);
    }

    @Transactional(readOnly = true)
    public List<PurchaseDTO> findAll() {
        return purchaseRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Purchase toEntity(PurchaseDTO dto) {
        Purchase p = new Purchase();
        p.setId(dto.getId());
        p.setDate(dto.getDate() != null ? dto.getDate() : new Date());
        p.setPaid(dto.isPaid());
        p.setDelivered(dto.isDelivered());
        p.setArchived(dto.isArchived());

        if (dto.getUserId() != null) {
            User u = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserId()));
            p.setUser(u);
        }

        List<PurchaseLine> lines = dto.getLines() == null ? List.of() :
                dto.getLines().stream()
                        .map(l -> toEntityLine(l, p))
                        .collect(Collectors.toList());
        p.setLines(lines);
        return p;
    }

    private PurchaseLine toEntityLine(PurchaseLineDTO dto, Purchase purchase) {
        PurchaseLine pl = new PurchaseLine();
        pl.setId(dto.getId());
        Game g = gameRepository.findById(dto.getGameId())
                .orElseThrow(() -> new RuntimeException("Game not found: " + dto.getGameId()));
        pl.setGame(g);
        pl.setQuantity(dto.getQuantity());
        pl.setPurchase(purchase);
        return pl;
    }

    private PurchaseDTO toDto(Purchase p) {
        PurchaseDTO dto = PurchaseDTO.builder()
                .id(p.getId())
                .date(p.getDate())
                .paid(p.isPaid())
                .delivered(p.isDelivered())
                .archived(p.isArchived())
                .userId(p.getUser() != null ? p.getUser().getId() : null)
                .build();

        List<PurchaseLineDTO> lines = p.getLines() == null ? List.of() :
                p.getLines().stream().map(l -> {
                    PurchaseLineDTO ld = new PurchaseLineDTO();
                    ld.setId(l.getId());
                    ld.setGameId(l.getGame() != null ? l.getGame().getId() : null);
                    ld.setQuantity(l.getQuantity());
                    return ld;
                }).collect(Collectors.toList());
        dto.setLines(lines);
        return dto;
    }
}
