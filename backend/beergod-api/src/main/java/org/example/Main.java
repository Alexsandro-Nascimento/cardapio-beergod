package org.example;

import org.example.domain.model.Bebida;
import org.example.domain.model.Cardapio;
import org.example.domain.model.CategoriaProduto;
import org.example.domain.model.Produto;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        Produto coxinha = new Produto(1L, "Coxinha de Catupiry", "Massa de macaxeira crocante", 8.0, CategoriaProduto.COXINHAS);
        System.out.println("Produto novo: "+ coxinha.getNome());

        Bebida cerveja = new Bebida(2L, "Heineken", "Cerveja Pilsne Long Neck", 12.00, 330, "Heineken");
        Cardapio cardapio = new Cardapio();

        cardapio.adicionarProduto(coxinha);
        cardapio.adicionarProduto(cerveja);

        cardapio.exibirCardapio();

        IO.println("TESTE DE EXCLUSÃO");
        cardapio.removerProduto(2L);
        cardapio.removerProduto(99L);

        cardapio.exibirCardapio();

    }
}
