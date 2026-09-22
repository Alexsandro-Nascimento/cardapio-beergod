package org.example;

import org.example.domain.model.Bebida;
import org.example.domain.model.Cardapio;
import org.example.domain.model.CategoriaProduto;
import org.example.domain.model.Produto;
import org.example.domain.exception.RegraNegocioException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        System.out.println("=== TESTE 1: Criando Bebida Válida ===");
        try {
            Bebida ipa = new Bebida(1L, "IPA Artesanal", "Cerveja encorpada", 25.0, 500, "Beergod");
            ipa.exibirDetalhes();
            System.out.println("-> Produto criado com sucesso!\n");
        } catch (RegraNegocioException e) {
            System.out.println("Erro inesperado: " + e.getMessage());
        }

        System.out.println("=== TESTE 2: Tentando criar produto com preço inválido ===");
        try {
            Bebida produtoInvalido = new Bebida(2L, "Pilsen", "Gelada", -10.0, 350, "Beergod");
            produtoInvalido.exibirDetalhes();
        } catch (RegraNegocioException e) {
            System.out.println("-> Sucesso no bloqueio! Exceção capturada: " + e.getMessage() + "\n");
        }

        System.out.println("=== TESTE 3: Tentando criar bebida com marca em branco ===");
        try {
            Bebida bebidaSemMarca = new Bebida(3L, "Stout", "Cerveja preta", 18.0, 473, "   ");
            bebidaSemMarca.exibirDetalhes();
        } catch (RegraNegocioException e) {
            System.out.println("-> Sucesso no bloqueio! Exceção capturada: " + e.getMessage() + "\n");
        }
    }

}

