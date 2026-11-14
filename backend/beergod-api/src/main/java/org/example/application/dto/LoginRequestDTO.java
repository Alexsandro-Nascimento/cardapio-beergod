package org.example.application.dto;

public record LoginRequestDTO(
        String email,
        String senha
) {
}