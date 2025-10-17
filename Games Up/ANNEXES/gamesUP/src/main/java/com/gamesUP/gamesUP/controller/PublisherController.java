package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.PublisherDTO;
import com.gamesUP.gamesUP.service.PublisherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    @PostMapping
    public ResponseEntity<PublisherDTO> create(@Valid @RequestBody PublisherDTO dto) {
        PublisherDTO created = publisherService.create(dto);
        URI location = URI.create(String.format("/api/publishers/%d", created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublisherDTO> update(@PathVariable Long id, @Valid @RequestBody PublisherDTO dto) {
        PublisherDTO updated = publisherService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        publisherService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherDTO> findById(@PathVariable Long id) {
        PublisherDTO dto = publisherService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<PublisherDTO>> findAll() {
        return ResponseEntity.ok(publisherService.findAll());
    }
}
