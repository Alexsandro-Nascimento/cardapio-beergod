package org.example.application.dto;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        String descricao,
        Double preco,
        String categoria,
        Integer tamanhoML,
        String marca
) {}
