package org.example.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.application.security.TokenService;
import org.example.domain.repository.AdministradorRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final AdministradorRepository administradorRepository;

    public JwtAuthenticationFilter(TokenService tokenService,
                                   AdministradorRepository administradorRepository) {
        this.tokenService = tokenService;
        this.administradorRepository = administradorRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = recuperarToken(request);

        if (token != null) {
            String email = tokenService.validarTokenEObterEmail(token);
            if (email != null) {
                administradorRepository.buscarPorEmail(email)
                        .filter(admin -> admin.isAtivo())
                        .ifPresent(admin -> {
                            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + admin.getPerfil().name()));
                            var authentication = new UsernamePasswordAuthenticationToken(admin, null, authorities);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        });
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}