package com.backend.gs.controller;

import com.backend.gs.dao.UserDao;
import com.backend.gs.dto.AuthResponse;
import com.backend.gs.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String username = authentication.getName();

        var optionalUser = userDao.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("Usuário não encontrado.");
        }

        User user = optionalUser.get();

        AuthResponse response = new AuthResponse(
                "Perfil carregado com sucesso",
                true,
                user.getId(),
                user.getUsername()
        );

        return ResponseEntity.ok(response);
    }
}
