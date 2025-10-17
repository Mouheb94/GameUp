package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.GameDTO;
import com.gamesUP.gamesUP.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameDTO> create(@Valid @RequestBody GameDTO gameDTO) {
        GameDTO created = gameService.create(gameDTO);
        URI location = URI.create(String.format("/api/games/%d", created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameDTO> update(@PathVariable Long id, @Valid @RequestBody GameDTO gameDTO) {
        GameDTO updated = gameService.update(id, gameDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gameService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> findById(@PathVariable Long id) {
        GameDTO dto = gameService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<GameDTO>> findAll(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Long authorId) {

        List<GameDTO> list;
        if (nom != null && !nom.isEmpty()) {
            list = gameService.findByNomContaining(nom);
        } else if (genre != null && !genre.isEmpty()) {
            list = gameService.findByGenre(genre);
        } else if (authorId != null) {
            list = gameService.findByAuthorId(authorId);
        } else {
            list = gameService.findAll();
        }
        return ResponseEntity.ok(list);
    }
}
