package org.example.domain.model;

import java.util.ArrayList;
import java.util.List;
// CLASSE E ATRIBUTOS
public class Cardapio {
    private List<Produto> produtos;

    //CONSTRUTOR
    public Cardapio(){
        this.produtos = new ArrayList<>();
    }

    //MÉTODOS
    public void adicionarProduto(Produto produto){
        this.produtos.add(produto);
        IO.println(produto.getNome()+" foi adicionado ao cardápio!");
    }

    public void exibirCardapio(){
        IO.println("\n=== CARDÁPIO ===");

        for(Produto produto : produtos){
            produto.exibirDetalhes();
            IO.println("---------------------------");
        }
    }

    public Produto buscarProdutoPorID(Long id){
        for (Produto produto : produtos){
            if(produto.getId().equals(id)){
                return produto;
            }
        }
        return null;
    }

    public void removerProduto(Long id){
        Produto produtoEncontrado = buscarProdutoPorID(id);

        if (produtoEncontrado != null){
            produtos.remove(produtoEncontrado);
            IO.println("O produto " + produtoEncontrado.getNome() + " foi removido!");
        }else{
            IO.println("Erro: Produto com ID " + id + " não encontrado!");
        }
    }
}
