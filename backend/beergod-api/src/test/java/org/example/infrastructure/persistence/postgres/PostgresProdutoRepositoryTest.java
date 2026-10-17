package org.example.infrastructure.persistence.postgres;

import org.example.domain.model.Bebida;
import org.example.domain.model.Produto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostgresProdutoRepositoryTest {
    @Test
    void deveSalvarEBuscarBebidaComSucesso() {
        PostgresProdutoRepository repository = new PostgresProdutoRepository();

        // 1. Cria uma bebida de teste
        Bebida novaBebida = new Bebida(
                null,
                "Cerveja IPA JUnit",
                "Testando persistência via JUnit",
                18.50,
                473,
                "BeerGod Test"
        );

        // 2. Salva no banco do Neon
        Produto bebidaSalva = repository.salvar(novaBebida);

        // 3. Valida se gerou um ID (o que prova que foi inserido no banco)
        assertNotNull(bebidaSalva.getId(), "O ID gerado pelo banco não deveria ser nulo!");

        // 4. Busca pelo ID recém-criado
        var produtoEncontrado = repository.buscarPorId(bebidaSalva.getId());

        assertTrue(produtoEncontrado.isPresent(), "O produto deveria existir no banco");
        assertEquals("Cerveja IPA JUnit", produtoEncontrado.get().getNome());
        assertEquals(18.50, produtoEncontrado.get().getPreco());

        // 5. Testa a listagem geral
        List<Produto> todos = repository.buscarTodos();
        assertFalse(todos.isEmpty(), "A lista de produtos não deveria estar vazia");
    }
}
