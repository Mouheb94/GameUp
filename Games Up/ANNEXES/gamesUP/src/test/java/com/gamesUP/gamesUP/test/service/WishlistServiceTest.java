package com.gamesUP.gamesUP.test.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.gamesUP.gamesUP.dto.WishlistDTO;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.entity.Wishlist;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.repository.WishlistRepository;
import com.gamesUP.gamesUP.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private WishlistService wishlistService;

    @Test
    void shouldCreateWishlistSuccess() {
        Long userId = 1L;
        Long g1 = 2L;
        Long g2 = 3L;

        WishlistDTO dto = WishlistDTO.builder()
                .userId(userId)
                .gameIds(Set.of(g1, g2))
                .build();

        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Game game1 = Game.builder().id(g1).build();
        Game game2 = Game.builder().id(g2).build();
        when(gameRepository.findById(g1)).thenReturn(Optional.of(game1));
        when(gameRepository.findById(g2)).thenReturn(Optional.of(game2));

        Wishlist saved = Wishlist.builder()
                .id(10L)
                .user(user)
                .games(Set.of(game1, game2))
                .build();
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(saved);

        WishlistDTO result = wishlistService.create(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(2, result.getGameIds().size());
        assertTrue(result.getGameIds().contains(g1));
        assertTrue(result.getGameIds().contains(g2));
        verify(userRepository).findById(userId);
        verify(gameRepository).findById(g1);
        verify(gameRepository).findById(g2);
        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void shouldThrowWhenUserNotFoundOnCreate() {
        Long userId = 99L;
        WishlistDTO dto = WishlistDTO.builder().userId(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> wishlistService.create(dto));
        verify(userRepository).findById(userId);
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenGameNotFoundOnCreate() {
        Long userId = 1L;
        Long missingGame = 77L;
        WishlistDTO dto = WishlistDTO.builder()
                .userId(userId)
                .gameIds(Set.of(missingGame))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(gameRepository.findById(missingGame)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> wishlistService.create(dto));
        verify(userRepository).findById(userId);
        verify(gameRepository).findById(missingGame);
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    void shouldUpdateWishlistSuccess() {
        Long id = 5L;
        Long oldUserId = 1L;
        Long newUserId = 2L;
        Long newGameId = 9L;

        User oldUser = User.builder().id(oldUserId).build();
        Wishlist existing = Wishlist.builder()
                .id(id)
                .user(oldUser)
                .games(Set.of())
                .build();
        when(wishlistRepository.findById(id)).thenReturn(Optional.of(existing));

        User newUser = User.builder().id(newUserId).build();
        when(userRepository.findById(newUserId)).thenReturn(Optional.of(newUser));
        Game newGame = Game.builder().id(newGameId).build();
        when(gameRepository.findById(newGameId)).thenReturn(Optional.of(newGame));

        Wishlist saved = Wishlist.builder()
                .id(id)
                .user(newUser)
                .games(Set.of(newGame))
                .build();
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(saved);

        WishlistDTO toUpdate = WishlistDTO.builder()
                .userId(newUserId)
                .gameIds(Set.of(newGameId))
                .build();

        WishlistDTO result = wishlistService.update(id, toUpdate);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(newUserId, result.getUserId());
        assertEquals(1, result.getGameIds().size());
        assertTrue(result.getGameIds().contains(newGameId));
        verify(wishlistRepository).findById(id);
        verify(userRepository).findById(newUserId);
        verify(gameRepository).findById(newGameId);
        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void shouldThrowWhenUpdateNotFound() {
        Long id = 42L;
        when(wishlistRepository.findById(id)).thenReturn(Optional.empty());
        WishlistDTO dto = WishlistDTO.builder().userId(1L).build();

        assertThrows(RuntimeException.class, () -> wishlistService.update(id, dto));
        verify(wishlistRepository).findById(id);
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    void shouldDeleteSuccess() {
        Long id = 7L;
        when(wishlistRepository.existsById(id)).thenReturn(true);

        wishlistService.delete(id);

        verify(wishlistRepository).existsById(id);
        verify(wishlistRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        Long id = 8L;
        when(wishlistRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> wishlistService.delete(id));
        verify(wishlistRepository).existsById(id);
        verify(wishlistRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldFindByIdSuccess() {
        Long id = 11L;
        User user = User.builder().id(3L).build();
        Game g = Game.builder().id(4L).build();
        Wishlist w = Wishlist.builder().id(id).user(user).games(Set.of(g)).build();
        when(wishlistRepository.findById(id)).thenReturn(Optional.of(w));

        WishlistDTO dto = wishlistService.findById(id);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals(3L, dto.getUserId());
        assertEquals(1, dto.getGameIds().size());
        assertTrue(dto.getGameIds().contains(4L));
        verify(wishlistRepository).findById(id);
    }

    @Test
    void shouldThrowWhenFindByIdNotFound() {
        Long id = 12L;
        when(wishlistRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> wishlistService.findById(id));
        verify(wishlistRepository).findById(id);
    }

    @Test
    void shouldFindAll() {
        User u1 = User.builder().id(1L).build();
        User u2 = User.builder().id(2L).build();
        Wishlist w1 = Wishlist.builder().id(1L).user(u1).games(Set.of()).build();
        Wishlist w2 = Wishlist.builder().id(2L).user(u2).games(Set.of()).build();
        when(wishlistRepository.findAll()).thenReturn(List.of(w1, w2));

        List<WishlistDTO> list = wishlistService.findAll();

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(1L, list.get(0).getUserId());
        assertEquals(2L, list.get(1).getId());
        assertEquals(2L, list.get(1).getUserId());
        verify(wishlistRepository).findAll();
    }

    @Test
    void shouldFindByUserSuccess() {
        Long userId = 5L;
        User user = User.builder().id(userId).build();
        Game g = Game.builder().id(6L).build();
        Wishlist w = Wishlist.builder().id(20L).user(user).games(Set.of(g)).build();
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(w));

        WishlistDTO dto = wishlistService.findByUserId(userId);

        assertNotNull(dto);
        assertEquals(20L, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertTrue(dto.getGameIds().contains(6L));
        verify(wishlistRepository).findByUserId(userId);
    }

    @Test
    void shouldThrowWhenFindByUserNotFound() {
        Long userId = 99L;
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> wishlistService.findByUserId(userId));
        verify(wishlistRepository).findByUserId(userId);
    }
}
