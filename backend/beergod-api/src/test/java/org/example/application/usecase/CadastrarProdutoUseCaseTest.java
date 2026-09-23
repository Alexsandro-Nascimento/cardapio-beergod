package org.example.application.usecase;

import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Bebida;
import org.example.domain.model.Produto;
import org.example.infrastructure.persistence.inmemory.InMemoryProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CadastrarProdutoUseCaseTest {
    private InMemoryProdutoRepository repository;
    private CadastrarProdutoUseCase useCase;

    // Executado antes de CADA teste para garantir um ambiente limpo e isolado
    @BeforeEach
    void setUp() {
        repository = new InMemoryProdutoRepository();
        useCase = new CadastrarProdutoUseCase(repository);
    }

    @Test
    @DisplayName("Deve cadastrar produto com sucesso quando os dados forem válidos")
    void deveCadastrarProdutoComSucesso() {
        // 1. Cenário (Arrange)
        Bebida bebida = new Bebida(null, "Heineken 600ml", "Cerveja Pilsen 600ml", 12.50, 600, "Lager");

        // 2. Ação (Act)
        Produto produtoSalvo = useCase.executar(bebida);

        // 3. Validação (Assert)
        assertNotNull(produtoSalvo.getId(), "O ID não deveria ser nulo após salvar");
        assertEquals("Heineken 600ml", produtoSalvo.getNome());
        assertEquals(12.50, produtoSalvo.getPreco());
        assertEquals(1, repository.buscarTodos().size(), "O repositório deve conter 1 produto");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar produto com preço inválido")
    void deveLancarExcecaoQuandoPrecoForInvalido() {
        // Valida se a criação de um produto inválido realmente dispara a RegraNegocioException
        assertThrows(RegraNegocioException.class, () -> {
            new Bebida(null, "Cerveja Inválida", "Descrição", -5.00, 600, "Pilsen");
        }, "Deveria ter lançado RegraNegocioException devido ao preço negativo");
    }
}
