package org.example.application.usecase;

import org.example.domain.model.Produto;
import org.example.domain.repository.ProdutoRepository;

import java.util.List;

public class ListarProdutosUseCase {
    private final ProdutoRepository produtoRepository;

    public ListarProdutosUseCase(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> executar() {
        return produtoRepository.buscarTodos();
    }
}
