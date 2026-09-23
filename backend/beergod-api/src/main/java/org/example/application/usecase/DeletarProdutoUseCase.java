package org.example.application.usecase;


import org.example.domain.exception.RegraNegocioException;
import org.example.domain.repository.ProdutoRepository;

public class DeletarProdutoUseCase {
    private final ProdutoRepository produtoRepository;

    public DeletarProdutoUseCase(ProdutoRepository produtoRepository){ this.produtoRepository = produtoRepository; }

    public void executar(Long id){
        produtoRepository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Não foi possível deletar o produto atráves do ID: "+ id));

        produtoRepository.deletar(id);
    }
}
