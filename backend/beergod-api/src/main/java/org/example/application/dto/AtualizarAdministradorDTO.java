package org.example.application.dto;

public record AtualizarAdministradorDTO(
        String nome,
        String email,
        String senha,
        Boolean ativo,
        String perfil
) {
}