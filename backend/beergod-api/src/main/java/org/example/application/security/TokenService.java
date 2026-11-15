package org.example.application.security;

import org.example.domain.model.Administrador;

public interface TokenService {
    String gerarToken(Administrador admin);
    String validarTokenEObterEmail(String token);
}