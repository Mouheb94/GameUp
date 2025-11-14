package com.gamesUP.gamesUP.repository;

import com.gamesUP.gamesUP.entity.Avis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvisRepository extends JpaRepository<Avis, Long> {
    List<Avis> findByGameId(Long gameId);
    List<Avis> findByNoteBetween(int min, int max);
}