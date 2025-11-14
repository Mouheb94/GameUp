package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.AuthDTO;
import com.gamesUP.gamesUP.service.AuthService;
import com.gamesUP.gamesUP.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gamesUP/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AuthDTO authDTO) {
        String token = authService.login(authDTO.getEmail(), authDTO.getPassword());
        return ResponseEntity.ok(token);
    }
}
