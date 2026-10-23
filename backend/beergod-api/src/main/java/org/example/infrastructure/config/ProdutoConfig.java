package org.example.infrastructure.config;
import org.example.application.usecase.BuscarProdutoPorIdUseCase;
import org.example.application.usecase.CadastrarProdutoUseCase;
import org.example.application.usecase.DeletarProdutoUseCase;
import org.example.application.usecase.ListarProdutosUseCase;
import org.example.domain.repository.ProdutoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProdutoConfig {
    @Bean
    public CadastrarProdutoUseCase cadastrarProdutoUseCase(ProdutoRepository produtoRepository) {
        return new CadastrarProdutoUseCase(produtoRepository);
    }

    @Bean
    public BuscarProdutoPorIdUseCase buscarProdutoPorIdUseCase(ProdutoRepository produtoRepository) {
        return new BuscarProdutoPorIdUseCase(produtoRepository);
    }

    @Bean
    public ListarProdutosUseCase listarProdutosUseCase(ProdutoRepository produtoRepository) {
        return new ListarProdutosUseCase(produtoRepository);
    }

    @Bean
    public DeletarProdutoUseCase deletarProdutoUseCase(ProdutoRepository produtoRepository) {
        return new DeletarProdutoUseCase(produtoRepository);
    }
}
