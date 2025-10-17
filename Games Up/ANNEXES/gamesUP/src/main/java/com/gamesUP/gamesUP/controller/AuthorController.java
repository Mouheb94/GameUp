package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.AuthorDTO;
import com.gamesUP.gamesUP.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<AuthorDTO> create(@Valid @RequestBody AuthorDTO authorDTO) {
        AuthorDTO created = authorService.create(authorDTO);
        URI location = URI.create(String.format("/api/authors/%d", created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDTO> update(@PathVariable Long id, @Valid @RequestBody AuthorDTO authorDTO) {
        AuthorDTO updated = authorService.update(id, authorDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> findById(@PathVariable Long id) {
        AuthorDTO dto = authorService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<AuthorDTO>> findAll() {
        List<AuthorDTO> list = authorService.findAll();
        return ResponseEntity.ok(list);
    }
}
