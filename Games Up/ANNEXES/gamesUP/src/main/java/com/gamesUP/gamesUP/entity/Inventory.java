package com.gamesUP.gamesUP.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Map;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ElementCollection
	@CollectionTable(name = "inventory_stock", joinColumns = @JoinColumn(name = "inventory_id"))
	@MapKeyJoinColumn(name = "game_id")
	@Column(name = "quantity")
	private Map<Game, Integer> stock;
}