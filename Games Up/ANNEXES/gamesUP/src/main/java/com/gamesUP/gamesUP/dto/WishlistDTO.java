package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistDTO implements Serializable {

    private Long id;

    @NotNull(message = "L'id de l'utilisateur est requis")
    private Long userId;

    private Set<@NotNull(message = "L'id du jeu ne peut pas être nul") Long> gameIds;
}