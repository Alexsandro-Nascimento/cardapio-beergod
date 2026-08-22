package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        Produto coxinha = new Produto(1L, "Coxinha de Catupiry", "Massa de macaxeira crocante", 8.0, CategoriaProduto.COXINHAS);
        System.out.println("Produto novo: "+ coxinha.getNome());

        coxinha.setPreco(10.00);

        IO.println("Novo preço: "+ coxinha.getPreco());

        Bebida cerveja = new Bebida(2L, "Heineken", "Cerveja Pilsne Long Neck", 12.00, 330, "Heineken");

        IO.println("Nova bebida: "+ cerveja.getNome() + " | Marca: "+ cerveja.getMarca());

        IO.println("O produto está ativo? "+ cerveja.isAtivo());

        IO.println("Categoria da Coxinha: "+ coxinha.getCategoria());
        IO.println("Categoria da Bebida: "+ cerveja.getCategoria());
    }
}
