package org.example.application.dto;

import java.math.BigDecimal;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        String imagemUrl,
        boolean ativo,
        Long categoriaId,
        String categoriaNome,
        String tipo,
        Integer tamanhoML,
        String marca
) {
}