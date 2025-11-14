package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("This user does not exist with the email: " + email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }
        return jwtUtil.generateToken(user.getEmail());
    }
}
