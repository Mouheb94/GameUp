package com.gamesUP.gamesUP.repository;

import com.gamesUP.gamesUP.entity.PurchaseLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseLineRepository extends JpaRepository<PurchaseLine, Long> {
}