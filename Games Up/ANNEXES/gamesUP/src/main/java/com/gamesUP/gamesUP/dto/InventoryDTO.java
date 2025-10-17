package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDTO implements Serializable {

    private Long id;

    @NotNull(message = "Le stock est requis")
    private Map<
            @NotNull(message = "L'id du jeu est requis") Long,
            @NotNull(message = "La quantité est requise")
            @Min(value = 0, message = "La quantité doit être au minimum 0") Integer> stock;
}