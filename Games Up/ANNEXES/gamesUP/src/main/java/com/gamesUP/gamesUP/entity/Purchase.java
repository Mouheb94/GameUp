package com.gamesUP.gamesUP.entity;

import java.util.Date;
import java.util.List;

public class Purchase {

	
	List<PurchaseLine> line;
	Date date;
	boolean paid;
	boolean delivered;
	boolean archived;
}
