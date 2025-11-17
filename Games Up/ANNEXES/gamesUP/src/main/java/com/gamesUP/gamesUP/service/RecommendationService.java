package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.RecommendationDTO;
import com.gamesUP.gamesUP.dto.UserDataDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.List;

@Service
public class RecommendationService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${python.recommendation.url:http://localhost:8000/recommendations/}")
    private String pythonUrl;

    public List<RecommendationDTO> getRecommendations(UserDataDTO userData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDataDTO> request = new HttpEntity<>(userData, headers);

        ResponseEntity<RecommendationsResponse> resp = restTemplate.postForEntity(pythonUrl, request, RecommendationsResponse.class);
        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            return resp.getBody().getRecommendations();
        }
        throw new RuntimeException("Erreur lors de l'appel à l'API de recommandation Python: " + resp.getStatusCode());
    }

    private static class RecommendationsResponse {
        private List<RecommendationDTO> recommendations;
        public List<RecommendationDTO> getRecommendations() { return recommendations; }
        public void setRecommendations(List<RecommendationDTO> recommendations) { this.recommendations = recommendations; }
    }
}
