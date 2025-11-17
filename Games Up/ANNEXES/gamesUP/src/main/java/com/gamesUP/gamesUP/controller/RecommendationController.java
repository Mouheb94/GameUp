package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.RecommendationDTO;
import com.gamesUP.gamesUP.dto.UserDataDTO;
import com.gamesUP.gamesUP.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<List<RecommendationDTO>> getRecommendations(@RequestBody UserDataDTO userData) {
        List<RecommendationDTO> recs = recommendationService.getRecommendations(userData);
        return ResponseEntity.ok(recs);
    }
}
