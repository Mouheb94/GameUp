// java
package com.gamesUP.gamesUP.security;

import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) { this.userService = userService; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userService.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String roleName = u.getRole() != null ? u.getRole().name() : "CUSTOMER";
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);
        return new org.springframework.security.core.userdetails.User(u.getEmail(), u.getPassword(), Collections.singletonList(authority));
    }
}
