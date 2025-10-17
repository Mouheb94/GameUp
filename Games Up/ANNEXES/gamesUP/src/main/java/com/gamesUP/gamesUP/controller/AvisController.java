package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.AvisDTO;
import com.gamesUP.gamesUP.service.AvisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/avis")
@RequiredArgsConstructor
public class AvisController {

    private final AvisService avisService;

    @PostMapping
    public ResponseEntity<AvisDTO> create(@Valid @RequestBody AvisDTO avisDTO) {
        AvisDTO created = avisService.create(avisDTO);
        URI location = URI.create(String.format("/api/avis/%d", created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvisDTO> update(@PathVariable Long id, @Valid @RequestBody AvisDTO avisDTO) {
        AvisDTO updated = avisService.update(id, avisDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        avisService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvisDTO> findById(@PathVariable Long id) {
        AvisDTO dto = avisService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<AvisDTO>> findAll() {
        List<AvisDTO> list = avisService.findAll();
        return ResponseEntity.ok(list);
    }
}
