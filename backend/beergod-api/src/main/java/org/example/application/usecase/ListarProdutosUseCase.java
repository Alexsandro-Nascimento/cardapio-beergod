package org.example.application.usecase;

import org.example.application.dto.ProdutoResponseDTO;
import org.example.domain.model.Bebida;
import org.example.domain.model.Produto;
import org.example.domain.repository.ProdutoRepository;

import java.util.List;

public class ListarProdutosUseCase {
    private final ProdutoRepository produtoRepository;

    public ListarProdutosUseCase(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<ProdutoResponseDTO> executar() {
        List<Produto> produtos = produtoRepository.buscarTodos();

        return produtos.stream().map(produto -> {
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

            // Mapeamento genérico caso haja outro tipo de produto
            return new ProdutoResponseDTO(
                    produto.getId(),
                    produto.getNome(),
                    produto.getDescricao(),
                    produto.getPreco(),
                    produto.getCategoria().name(),
                    null,
                    null
            );
        }).toList();
    }
}