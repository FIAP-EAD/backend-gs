package com.backend.gs.controller;

import com.backend.gs.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @GetMapping("/profile")
    public ResponseEntity<AuthResponse> getProfile(Authentication authentication) {
        String username = authentication.getName();
        
        AuthResponse response = new AuthResponse();
        response.setSuccess(true);
        response.setMessage("Perfil do usuário");
        response.setUsername(username);
        
        return ResponseEntity.ok(response);
    }
}

