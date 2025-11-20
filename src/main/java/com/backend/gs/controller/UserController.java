package com.backend.gs.controller;

import com.backend.gs.dto.AuthResponse;
import com.backend.gs.model.User;
import com.backend.gs.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(404)
                    .body("Usuário não encontrado.");
        }

        AuthResponse response = new AuthResponse(
                "Perfil carregado com sucesso",
                true,
                user.getId(),
                user.getUsername()
        );

        return ResponseEntity.ok(response);
    }
}
