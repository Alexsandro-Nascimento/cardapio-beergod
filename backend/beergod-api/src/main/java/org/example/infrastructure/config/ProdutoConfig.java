package org.example.infrastructure.config;

import org.example.application.security.ContextoAutenticacao;
import org.example.application.usecase.AtualizarProdutoUseCase;
import org.example.application.usecase.CriarProdutoUseCase;
import org.example.application.usecase.InativarProdutoUseCase;
import org.example.application.usecase.ListarProdutosUseCase;
import org.example.domain.repository.CategoriaRepository;
import org.example.domain.repository.ProdutoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProdutoConfig {

    @Bean
    public CriarProdutoUseCase criarProdutoUseCase(ProdutoRepository produtoRepository,
                                                   CategoriaRepository categoriaRepository,
                                                   ContextoAutenticacao contextoAutenticacao) {
        return new CriarProdutoUseCase(produtoRepository, categoriaRepository, contextoAutenticacao);
    }

    @Bean
    public ListarProdutosUseCase listarProdutosUseCase(ProdutoRepository produtoRepository,
                                                       CategoriaRepository categoriaRepository) {
        return new ListarProdutosUseCase(produtoRepository, categoriaRepository);
    }

    @Bean
    public AtualizarProdutoUseCase atualizarProdutoUseCase(ProdutoRepository produtoRepository,
                                                           CategoriaRepository categoriaRepository,
                                                           ContextoAutenticacao contextoAutenticacao) {
        return new AtualizarProdutoUseCase(produtoRepository, categoriaRepository, contextoAutenticacao);
    }

    @Bean
    public InativarProdutoUseCase inativarProdutoUseCase(ProdutoRepository produtoRepository,
                                                         ContextoAutenticacao contextoAutenticacao) {
        return new InativarProdutoUseCase(produtoRepository, contextoAutenticacao);
    }
}