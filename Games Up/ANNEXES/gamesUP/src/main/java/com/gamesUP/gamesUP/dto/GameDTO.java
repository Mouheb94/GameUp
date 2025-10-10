package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GameDTO {

    private int id;

    @NotNull(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String nom;

    @NotNull(message = "Author is required")
    @Size(min = 2, max = 100, message = "Author must be between 2 and 100 characters")
    private String auteur;

    @NotNull(message = "Genre is required")
    private String genre;

    private int categoryId;
    private int publisherId;
    private int numEdition;
}