package org.example.application.usecase;

import org.example.application.dto.ProdutoRequestDTO;
import org.example.application.dto.ProdutoResponseDTO;
import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Bebida;
import org.example.domain.model.Categoria;
import org.example.domain.model.Produto;
import org.example.domain.repository.CategoriaRepository;
import org.example.domain.repository.ProdutoRepository;

public class AtualizarProdutoUseCase {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public AtualizarProdutoUseCase(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public ProdutoResponseDTO executar(Long id, ProdutoRequestDTO dto) {
        Produto existente = produtoRepository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado!"));

        Categoria categoria = categoriaRepository.buscarPorId(dto.categoriaId())
                .orElseThrow(() -> new RegraNegocioException("Categoria informada não existe!"));

        boolean statusAtivo = dto.ativo() != null ? dto.ativo() : existente.isAtivo();
        boolean isBebida = dto.tamanhoML() != null || (dto.marca() != null && !dto.marca().isBlank());

        Produto atualizado = isBebida
                ? new Bebida(existente.getId(), dto.nome(), dto.descricao(), dto.preco(), dto.imagemUrl(),
                statusAtivo, dto.categoriaId(), dto.tamanhoML() != null ? dto.tamanhoML() : 0, dto.marca())
                : new Produto(existente.getId(), dto.nome(), dto.descricao(), dto.preco(), dto.imagemUrl(),
                statusAtivo, dto.categoriaId());

        produtoRepository.atualizar(atualizado);

        Integer tamanhoML = (atualizado instanceof Bebida b) ? b.getTamanhoML() : null;
        String marca = (atualizado instanceof Bebida b) ? b.getMarca() : null;
        String tipo = (atualizado instanceof Bebida) ? "BEBIDA" : "PRODUTO";

        return new ProdutoResponseDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getDescricao(),
                atualizado.getPreco(),
                atualizado.getImagemUrl(),
                atualizado.isAtivo(),
                atualizado.getCategoriaId(),
                categoria.getNome(),
                tipo,
                tamanhoML,
                marca
        );
    }
}