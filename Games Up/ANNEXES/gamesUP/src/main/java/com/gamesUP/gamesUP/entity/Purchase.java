package com.gamesUP.gamesUP.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "purchase")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
	private List<PurchaseLine> lines;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	private boolean paid;
	private boolean delivered;
	private boolean archived;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
}
