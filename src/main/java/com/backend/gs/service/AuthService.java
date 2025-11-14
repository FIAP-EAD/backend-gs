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
        // Verificar se o username já existe
        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse("Username já está em uso", false);
        }

        // Verificar se o email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse("Email já está em uso", false);
        }

        // Criar novo usuário
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Salvar no banco de dados
        User savedUser = userRepository.save(user);

        // Gerar token JWT
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
        // Buscar usuário pelo username
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            return new AuthResponse("Username ou senha inválidos", false);
        }

        User user = userOptional.get();

        // Verificar senha
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponse("Username ou senha inválidos", false);
        }

        // Gerar token JWT
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

