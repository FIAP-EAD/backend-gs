package com.backend.gs.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class PublicEndpointAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                       AuthenticationException authException) throws IOException, ServletException {
        String path = request.getRequestURI();
        
        // Para endpoints públicos, não retorna erro - permite acesso anônimo
        if (path.startsWith("/api/auth/") || path.startsWith("/api/jobReport/callback/")) {
            // Não faz nada - permite que a requisição continue sem autenticação
            // O Spring Security vai processar o permitAll() depois
            return;
        }
        
        // Para outros endpoints, retorna 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Unauthorized\"}");
    }
}

