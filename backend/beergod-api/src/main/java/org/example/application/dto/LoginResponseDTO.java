package org.example.application.dto;

public record LoginResponseDTO(
        Long id,
        String nome,
        String email,
        boolean ativo,
        String perfil,
        String token
) {
}