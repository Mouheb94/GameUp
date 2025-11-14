package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.WishlistDTO;
import com.gamesUP.gamesUP.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<WishlistDTO> create(@Valid @RequestBody WishlistDTO dto) {
        WishlistDTO created = wishlistService.create(dto);
        URI location = URI.create(String.format("/api/wishlists/%d", created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<WishlistDTO> update(@PathVariable Long id, @Valid @RequestBody WishlistDTO dto) {
        WishlistDTO updated = wishlistService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        wishlistService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<WishlistDTO> findById(@PathVariable Long id) {
        WishlistDTO dto = wishlistService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WishlistDTO>> findAll() {
        return ResponseEntity.ok(wishlistService.findAll());
    }

    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<WishlistDTO> findByUser(@PathVariable Long userId) {
        WishlistDTO dto = wishlistService.findByUserId(userId);
        return ResponseEntity.ok(dto);
    }
}
