package org.example.application.usecase;

import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Bebida;
import org.example.domain.model.Produto;
import org.example.infrastructure.persistence.inmemory.InMemoryProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeletarProdutoUseCaseTest {

    private InMemoryProdutoRepository repository;
    private DeletarProdutoUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProdutoRepository();
        useCase = new DeletarProdutoUseCase(repository);
    }

    @Test
    @DisplayName("Deve deletar o produto com sucesso quando o ID existir")
    void deveDeletarProdutoComSucesso() {
        // Arrange: salva um produto no repositório
        Bebida bebida = new Bebida(null, "Skol 350ml", "Cerveja Pilsen", 3.99, 350, "Lager");
        Produto produtoSalvo = repository.salvar(bebida);

        // Act: deleta o produto pelo ID
        useCase.executar(produtoSalvo.getId());

        // Assert: garante que a lista de produtos agora está vazia (tamanho 0)
        assertEquals(0, repository.buscarTodos().size());
        assertTrue(repository.buscarPorId(produtoSalvo.getId()).isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar produto com ID inexistente")
    void deveLancarExcecaoQuandoDeletarIdInexistente() {
        // Act & Assert: tenta deletar um ID que não existe e garante que estoura exceção
        assertThrows(RegraNegocioException.class, () -> {
            useCase.executar(888L);
        });
    }
}