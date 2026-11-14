package org.example.application.usecase;

import org.example.application.dto.ProdutoRequestDTO;
import org.example.application.dto.ProdutoResponseDTO;
import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Bebida;
import org.example.domain.model.Categoria;
import org.example.domain.model.Produto;
import org.example.domain.repository.CategoriaRepository;
import org.example.domain.repository.ProdutoRepository;

public class CriarProdutoUseCase {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public CriarProdutoUseCase(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public ProdutoResponseDTO executar(ProdutoRequestDTO dto) {
        Categoria categoria = categoriaRepository.buscarPorId(dto.categoriaId())
                .orElseThrow(() -> new RegraNegocioException("Categoria informada não existe!"));

        boolean isBebida = dto.tamanhoML() != null || (dto.marca() != null && !dto.marca().isBlank());

        Produto novo = isBebida
                ? new Bebida(dto.nome(), dto.descricao(), dto.preco(), dto.imagemUrl(), dto.categoriaId(),
                dto.tamanhoML() != null ? dto.tamanhoML() : 0, dto.marca())
                : new Produto(dto.nome(), dto.descricao(), dto.preco(), dto.imagemUrl(), dto.categoriaId());

        Produto salvo = produtoRepository.salvar(novo);

        Integer tamanhoML = (salvo instanceof Bebida b) ? b.getTamanhoML() : null;
        String marca = (salvo instanceof Bebida b) ? b.getMarca() : null;
        String tipo = (salvo instanceof Bebida) ? "BEBIDA" : "PRODUTO";

        return new ProdutoResponseDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getDescricao(),
                salvo.getPreco(),
                salvo.getImagemUrl(),
                salvo.isAtivo(),
                salvo.getCategoriaId(),
                categoria.getNome(),
                tipo,
                tamanhoML,
                marca
        );
    }
}