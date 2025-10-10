package com.gamesUP.gamesUP.repository;

import com.gamesUP.gamesUP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
