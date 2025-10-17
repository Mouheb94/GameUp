package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameDTO implements Serializable {

    private Long id;

    @NotBlank(message = "Le nom est requis")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @NotNull(message = "L'auteur est requis")
    private Long authorId;

    @NotBlank(message = "Le genre est requis")
    private String genre;

    private Long categoryId;
    private Long publisherId;
    private Integer numEdition;
}