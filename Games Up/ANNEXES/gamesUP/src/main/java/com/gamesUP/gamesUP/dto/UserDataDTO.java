package com.gamesUP.gamesUP.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDataDTO {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("purchases")
    private List<UserPurchaseDTO> purchases;
}