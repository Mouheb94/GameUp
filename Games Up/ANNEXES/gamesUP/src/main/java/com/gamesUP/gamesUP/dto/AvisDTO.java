package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvisDTO {

    private Long id;

    @NotNull(message = "Comment is required")
    @Size(min = 2, max = 500, message = "Comment must be between 2 and 500 characters")
    private String commentaire;

    @NotNull(message = "Rating is required")
    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer note;
}