package org.example;

import org.example.application.usecase.CadastrarProdutoUseCase;
import org.example.application.usecase.ListarProdutosUseCase;
import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Bebida;
import org.example.domain.model.Produto;
import org.example.infrastructure.persistence.inmemory.InMemoryProdutoRepository;

import java.util.List;

public class Main {
    static void main() {
        // 1. Instanciamos a Infraestrutura (Repositório em memória)
        InMemoryProdutoRepository produtoRepository = new InMemoryProdutoRepository();

        // 2. Instanciamos os Casos de Uso injetando a dependência do Repositório
        CadastrarProdutoUseCase cadastrarProduto = new CadastrarProdutoUseCase(produtoRepository);
        ListarProdutosUseCase listarProdutos = new ListarProdutosUseCase(produtoRepository);

        System.out.println("=== 1. TESTANDO CADASTRO COM SUCESSO ===");
        try {
            Bebida heineken = new Bebida(null, "Heineken 600ml", "Cerveja Pilsen garrafa 600ml", 12.50, 600, "Lager");
            Bebida ipa = new Bebida(null, "IPA Artesanal", "Cerveja forte e lupulada", 22.00, 500, "IPA");

            cadastrarProduto.executar(heineken);
            cadastrarProduto.executar(ipa);

            System.out.println("Produtos cadastrados com sucesso!");
        } catch (RegraNegocioException e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
        }

        System.out.println("\n=== 2. LISTANDO PRODUTOS CADASTRADOS ===");
        List<Produto> produtos = listarProdutos.executar();
        for (Produto p : produtos) {
            System.out.println("ID: " + p.getId() + " | Nome: " + p.getNome() + " | Preço: R$ " + p.getPreco());
        }

        System.out.println("\n=== 3. TESTANDO VALIDAÇÃO FAIL-FAST (PREÇO INVÁLIDO) ===");
        try {
            // Tentativa de criar produto com preço negativo
            Bebida cervejaInvalida = new Bebida(null, "Cerveja Sem Preço", "Descrição teste", -5.00, 350, "Pilsen");
            cadastrarProduto.executar(cervejaInvalida);
        } catch (RegraNegocioException e) {
            System.out.println("Bloqueado com sucesso pelo Domínio: " + e.getMessage());
        }
    }

}

