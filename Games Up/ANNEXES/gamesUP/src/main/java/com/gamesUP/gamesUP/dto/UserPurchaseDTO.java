package com.gamesUP.gamesUP.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPurchaseDTO {
    @JsonProperty("game_id")
    private Long gameId;

    @JsonProperty("rating")
    private Double rating;
}