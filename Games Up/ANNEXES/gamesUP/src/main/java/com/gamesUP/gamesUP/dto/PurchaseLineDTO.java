package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseLineDTO implements Serializable {

    private Long id;

    @NotNull(message = "L'id du jeu est requis")
    private Long gameId;

    @Min(value = 1, message = "La quantité doit être au minimum 1")
    private int quantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix doit être positif")
    private double price;
}