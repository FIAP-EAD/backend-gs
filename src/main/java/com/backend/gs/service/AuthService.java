package com.backend.gs.service;

import com.backend.gs.dto.AuthResponse;
import com.backend.gs.dto.LoginRequest;
import com.backend.gs.dto.RegisterRequest;
import com.backend.gs.model.User;
import com.backend.gs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse("Username já está em uso", false);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse("Email já está em uso", false);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser.getUsername(), savedUser.getId());

        return new AuthResponse(
            "Usuário registrado com sucesso",
            true,
            savedUser.getId(),
            savedUser.getUsername(),
            token
        );
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            return new AuthResponse("Username ou senha inválidos", false);
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponse("Username ou senha inválidos", false);
        }

        String token = jwtService.generateToken(user.getUsername(), user.getId());

        return new AuthResponse(
            "Login realizado com sucesso",
            true,
            user.getId(),
            user.getUsername(),
            token
        );
    }
}