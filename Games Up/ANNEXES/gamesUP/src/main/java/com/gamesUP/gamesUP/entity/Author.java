package com.gamesUP.gamesUP.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


public class Author {

    public Long id;
    
    public String name;
    
    public List<Game> games;

}
