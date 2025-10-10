package com.gamesUP.gamesUP.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String nom;

    @Column(name = "author", nullable = false)
    private String auteur;

    @Column(name = "genre", nullable = false)
    private String genre;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @Column(name = "edition_number")
    private int numEdition;
}