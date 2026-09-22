package org.example.application.usecase;

import org.example.domain.model.Produto;
import org.example.domain.repository.ProdutoRepository;

public class CadastrarProdutoUseCase {
    private final ProdutoRepository produtoRepository;

    public CadastrarProdutoUseCase(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto executar(Produto produto){
        return produtoRepository.salvar(produto);
    }
}
