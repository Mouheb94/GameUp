package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    private Long id;

    @NotNull(message = "Type is required")
    @Size(min = 2, max = 100, message = "Type must be between 2 and 100 characters")
    private String type;
}