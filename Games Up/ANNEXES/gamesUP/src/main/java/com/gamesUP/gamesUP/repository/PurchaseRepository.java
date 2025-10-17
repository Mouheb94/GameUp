package com.gamesUP.gamesUP.repository;

import com.gamesUP.gamesUP.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUserId(Long userId);
    List<Purchase> findByPaid(boolean paid);
}