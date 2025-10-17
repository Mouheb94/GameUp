// java
package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.GameDTO;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.entity.Author;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final AuthorRepository authorRepository;

    public GameDTO create(GameDTO gameDTO) {
        Game game = toEntity(gameDTO);
        if (gameDTO.getAuthorId() != null) {
            Author author = authorRepository.findById(gameDTO.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Author not found: " + gameDTO.getAuthorId()));
            game.setAuthor(author);
        }
        Game saved = gameRepository.save(game);
        return toDto(saved);
    }

    public GameDTO update(Long id, GameDTO gameDTO) {
        Game existing = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found: " + id));
        existing.setNom(gameDTO.getNom());
        existing.setGenre(gameDTO.getGenre());
        if (gameDTO.getAuthorId() != null && !gameDTO.getAuthorId().equals(existing.getAuthor() != null ? existing.getAuthor().getId() : null)) {
            Author author = authorRepository.findById(gameDTO.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Author not found: " + gameDTO.getAuthorId()));
            existing.setAuthor(author);
        }
        Game saved = gameRepository.save(existing);
        return toDto(saved);
    }

    public void delete(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new RuntimeException("Game not found: " + id);
        }
        gameRepository.deleteById(id);
    }

    public GameDTO findById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found: " + id));
        return toDto(game);
    }

    public List<GameDTO> findAll() {
        return gameRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<GameDTO> findByNomContaining(String nom) {
        return gameRepository.findByNomContainingIgnoreCase(nom).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<GameDTO> findByGenre(String genre) {
        return gameRepository.findByGenre(genre).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<GameDTO> findByAuthorId(Long authorId) {
        return gameRepository.findByAuthorId(authorId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Game toEntity(GameDTO dto) {
        Game g = new Game();
        g.setId(dto.getId());
        g.setNom(dto.getNom());
        g.setGenre(dto.getGenre());
        return g;
    }

    private GameDTO toDto(Game g) {
        return GameDTO.builder()
                .id(g.getId())
                .nom(g.getNom())
                .genre(g.getGenre())
                .authorId(g.getAuthor() != null ? g.getAuthor().getId() : null)
                .build();
    }
}
