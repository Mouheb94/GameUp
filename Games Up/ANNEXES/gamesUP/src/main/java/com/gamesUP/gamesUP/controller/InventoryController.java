package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.InventoryDTO;
import com.gamesUP.gamesUP.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryDTO> create(@Valid @RequestBody InventoryDTO dto) {
        InventoryDTO created = inventoryService.create(dto);
        URI location = URI.create(String.format("/api/inventories/%d", created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryDTO> update(@PathVariable Long id, @Valid @RequestBody InventoryDTO dto) {
        InventoryDTO updated = inventoryService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<InventoryDTO> findById(@PathVariable Long id) {
        InventoryDTO dto = inventoryService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<InventoryDTO>> findAll() {
        return ResponseEntity.ok(inventoryService.findAll());
    }
}
