package org.example.application.dto;

public record CriarProdutoDTO(
        String nome,
        String descricao,
        Double preco,
        String categoria,
        Integer tamanhoML,
        String marca
){}



