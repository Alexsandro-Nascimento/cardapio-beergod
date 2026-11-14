package org.example.application.usecase;

import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Bebida;
import org.example.domain.model.Produto;
import org.example.infrastructure.persistence.inmemory.InMemoryProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class BuscarProdutoPorIdUseCaseTest {

    private InMemoryProdutoRepository repository;
    private BuscarProdutoPorIdUseCase useCase;

    @BeforeEach
    void setUp(){
        repository = new InMemoryProdutoRepository();
        useCase = new BuscarProdutoPorIdUseCase(repository);
    }

    @Test
    @DisplayName("Deve retornar o produto quando o ID existir no repositório")
    void deveRetornarProdutoQuandoIdExistir(){
        Bebida bebida = new Bebida(null, "Itaipava 350ml", "Cerveja Pilsen", 4.50, 350, "Lager");
        Produto produtoSalvo = repository.salvar(bebida);

        Produto produtoEncontrado = useCase.executar(produtoSalvo.getId());

        assertNotNull(produtoEncontrado, "O produto encontrado não deveria ser nulo");
        assertEquals("Itaipava 350ml", produtoEncontrado.getNome());
        assertEquals(4.50, produtoEncontrado.getPreco());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o ID não for encontrado")
    void deveLancarExcecaoQuandoIdNaoExistir() {
        assertThrows(RegraNegocioException.class, () -> {
            useCase.executar(999L);
        }, "Deveria ter lançado exceção para ID inexistente");
    }

}
