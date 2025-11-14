package com.gamesUP.gamesUP.repository;

import com.gamesUP.gamesUP.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByNomContainingIgnoreCase(String nom);
    List<Game> findByGenre(String genre);
    List<Game> findByAuthorId(Long authorId);
}