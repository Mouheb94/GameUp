package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvisDTO implements Serializable {

    private Long id;

    @NotBlank(message = "Le commentaire est requis")
    @Size(min = 2, max = 500, message = "Le commentaire doit contenir entre 2 et 500 caractères")
    private String commentaire;

    @NotNull(message = "La note est requise")
    @Min(value = 0, message = "La note doit être au minimum 0")
    @Max(value = 10, message = "La note doit être au maximum 10")
    private Integer note;

    @NotNull(message = "L'id du jeu est requis")
    private Long gameId;
}