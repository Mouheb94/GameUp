package com.gamesUP.gamesUP.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "avis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avis {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "comment", nullable = false)
	private String commentaire;

	@Column(name = "rating", nullable = false)
	private int note;

	@ManyToOne
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;
}
