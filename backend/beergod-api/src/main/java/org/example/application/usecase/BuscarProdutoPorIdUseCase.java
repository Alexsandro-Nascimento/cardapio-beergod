package org.example.application.usecase;

import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Produto;
import org.example.domain.repository.ProdutoRepository;

public class BuscarProdutoPorIdUseCase {
    private final ProdutoRepository produtoRepository;

    public BuscarProdutoPorIdUseCase(ProdutoRepository produtoRepository){ this.produtoRepository = produtoRepository; }

    public Produto executar(Long id){
        return produtoRepository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado com o ID: " + id));
    }
}
