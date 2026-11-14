package org.example.application.usecase;

import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Produto;
import org.example.domain.repository.ProdutoRepository;

public class InativarProdutoUseCase {

    private final ProdutoRepository produtoRepository;

    public InativarProdutoUseCase(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public void executar(Long id) {
        Produto produto = produtoRepository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado!"));

        produto.inativar();
        produtoRepository.atualizar(produto);
    }
}