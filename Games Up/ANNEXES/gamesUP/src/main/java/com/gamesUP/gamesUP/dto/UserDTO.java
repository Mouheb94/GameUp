package com.gamesUP.gamesUP.dto;

import com.gamesUP.gamesUP.enumeration.Role;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserDTO {
    private Long id;
    private String nom;
    private String email;
    private String password;
    private Role role;
}