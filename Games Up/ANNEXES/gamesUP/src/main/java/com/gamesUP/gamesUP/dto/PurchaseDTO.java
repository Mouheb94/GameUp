package com.gamesUP.gamesUP.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseDTO implements Serializable {

    private Long id;

    @NotNull(message = "Les lignes d'achat sont requises")
    private List<@Valid PurchaseLineDTO> lines;

    private Date date;

    private boolean paid;
    private boolean delivered;
    private boolean archived;

    @NotNull(message = "L'id de l'utilisateur est requis")
    private Long userId;
}