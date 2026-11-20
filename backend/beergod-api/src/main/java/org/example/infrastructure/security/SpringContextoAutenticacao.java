package org.example.infrastructure.security;

import org.example.application.security.ContextoAutenticacao;
import org.example.domain.model.Administrador;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringContextoAutenticacao implements ContextoAutenticacao {

    @Override
    public Optional<Administrador> getUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Administrador admin) {
            return Optional.of(admin);
        }
        return Optional.empty();
    }
}