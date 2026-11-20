package org.example.domain.model;

import org.example.domain.exception.RegraNegocioException;

public enum PerfilAdministrador {
    ADMIN_MASTER,
    ADMIN_BEBIDAS,
    ADMIN_COZINHA,
    FUNCIONARIO;

    public static PerfilAdministrador fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return ADMIN_MASTER;
        }
        try {
            return PerfilAdministrador.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegraNegocioException("Perfil de acesso inválido: " + valor);
        }
    }
}