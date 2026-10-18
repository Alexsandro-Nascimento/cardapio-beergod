package org.example.application.usecase;

import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Produto;
import org.example.domain.repository.ProdutoRepository;
import org.example.domain.model.Bebida;
import org.example.application.dto.ProdutoResponseDTO;

public class BuscarProdutoPorIdUseCase {
    private final ProdutoRepository produtoRepository;

    public BuscarProdutoPorIdUseCase(ProdutoRepository produtoRepository){ this.produtoRepository = produtoRepository; }

    public ProdutoResponseDTO executar(Long id){
        Produto produto = produtoRepository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado com o ID: " + id));

        if (produto instanceof Bebida bebida) {
            return new ProdutoResponseDTO(
                    bebida.getId(),
                    bebida.getNome(),
                    bebida.getDescricao(),
                    bebida.getPreco(),
                    bebida.getCategoria().name(),
                    bebida.getTamanhoML(),
                    bebida.getMarca()
            );
        }
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getCategoria().name(),
                null,
                null
        );
    }
}
