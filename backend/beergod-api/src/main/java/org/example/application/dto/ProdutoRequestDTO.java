package org.example.application.dto;

import java.math.BigDecimal;

public record ProdutoRequestDTO(
        String nome,
        String descricao,
        BigDecimal preco,
        String imagemUrl,
        Boolean ativo,
        Long categoriaId,
        Integer tamanhoML,
        String marca
) {
}