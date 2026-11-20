package org.example.application.security;

import org.example.domain.model.Administrador;
import java.util.Optional;

public interface ContextoAutenticacao {
    Optional<Administrador> getUsuarioLogado();
}