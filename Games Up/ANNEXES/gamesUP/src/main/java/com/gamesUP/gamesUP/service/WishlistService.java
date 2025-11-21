package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.WishlistDTO;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.entity.Wishlist;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public WishlistDTO create(WishlistDTO dto) {
        Wishlist w = toEntity(dto);
        Wishlist saved = wishlistRepository.save(w);
        return toDto(saved);
    }

    public WishlistDTO update(Long id, WishlistDTO dto) {
        Wishlist existing = wishlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wishlist not found: " + id));
        User u = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserId()));
        existing.setUser(u);
        Set<Game> games = dto.getGameIds() == null ? Set.of() :
                dto.getGameIds().stream()
                        .map(gameId -> gameRepository.findById(gameId)
                                .orElseThrow(() -> new RuntimeException("Game not found: " + gameId)))
                        .collect(Collectors.toSet());
        existing.setGames(games);
        Wishlist saved = wishlistRepository.save(existing);
        return toDto(saved);
    }

    public void delete(Long id) {
        if (!wishlistRepository.existsById(id)) {
            throw new RuntimeException("Wishlist not found: " + id);
        }
        wishlistRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public WishlistDTO findById(Long id) {
        Wishlist w = wishlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wishlist not found: " + id));
        return toDto(w);
    }

    @Transactional(readOnly = true)
    public List<WishlistDTO> findAll() {
        return wishlistRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WishlistDTO findByUserId(Long userId) {
        Wishlist w = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found for user: " + userId));
        return toDto(w);
    }

    private Wishlist toEntity(WishlistDTO dto) {
        Wishlist w = new Wishlist();
        w.setId(dto.getId());
        User u = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserId()));
        w.setUser(u);
        Set<Game> games = dto.getGameIds() == null ? Set.of() :
                dto.getGameIds().stream()
                        .map(gameId -> gameRepository.findById(gameId)
                                .orElseThrow(() -> new RuntimeException("Game not found: " + gameId)))
                        .collect(Collectors.toSet());
        w.setGames(games);
        return w;
    }

    private WishlistDTO toDto(Wishlist w) {
        WishlistDTO dto = new WishlistDTO();
        dto.setId(w.getId());
        dto.setUserId(w.getUser() != null ? w.getUser().getId() : null);
        Set<Long> gameIds = w.getGames() == null ? Set.of() :
                w.getGames().stream().map(Game::getId).collect(Collectors.toSet());
        dto.setGameIds(gameIds);
        return dto;
    }
}
