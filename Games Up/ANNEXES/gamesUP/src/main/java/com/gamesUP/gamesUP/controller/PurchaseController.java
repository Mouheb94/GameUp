package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.PurchaseDTO;
import com.gamesUP.gamesUP.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<PurchaseDTO> create(@Valid @RequestBody PurchaseDTO dto) {
        PurchaseDTO created = purchaseService.create(dto);
        URI location = URI.create(String.format("/api/purchases/%d", created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseDTO> update(@PathVariable Long id, @Valid @RequestBody PurchaseDTO dto) {
        PurchaseDTO updated = purchaseService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        purchaseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDTO> findById(@PathVariable Long id) {
        PurchaseDTO dto = purchaseService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<PurchaseDTO>> findAll() {
        return ResponseEntity.ok(purchaseService.findAll());
    }
}
