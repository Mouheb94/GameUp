package com.gamesUP.gamesUP.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDTO {
    @JsonProperty("game_id")
    private int game_id;
    @JsonProperty("rating")
    private double rating;

}
