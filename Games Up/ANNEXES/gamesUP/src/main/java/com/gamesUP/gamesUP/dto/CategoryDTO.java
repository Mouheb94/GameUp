package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO implements Serializable {

    private Long id;

    @NotBlank(message = "Le type est requis")
    @Size(min = 2, max = 100, message = "Le type doit contenir entre 2 et 100 caractères")
    private String type;
}