package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.PurchaseLineDTO;
import com.gamesUP.gamesUP.service.PurchaseLineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-lines")
@RequiredArgsConstructor
public class PurchaseLineController {

    private final PurchaseLineService purchaseLineService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<PurchaseLineDTO> create(
            @RequestParam(name = "purchaseId", required = false) Long purchaseId,
            @Valid @RequestBody PurchaseLineDTO dto) {
        PurchaseLineDTO created = purchaseLineService.create(purchaseId, dto);
        URI location = URI.create(String.format("/api/purchase-lines/%d", created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<PurchaseLineDTO> update(
            @PathVariable Long id,
            @RequestParam(name = "purchaseId", required = false) Long purchaseId,
            @Valid @RequestBody PurchaseLineDTO dto) {
        PurchaseLineDTO updated = purchaseLineService.update(id, purchaseId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        purchaseLineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<PurchaseLineDTO> findById(@PathVariable Long id) {
        PurchaseLineDTO dto = purchaseLineService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PurchaseLineDTO>> findAll() {
        return ResponseEntity.ok(purchaseLineService.findAll());
    }
}
