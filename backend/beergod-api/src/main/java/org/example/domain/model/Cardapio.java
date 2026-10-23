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
        System.out.println(produto.getNome()+" foi adicionado ao cardápio!");
    }

    public void exibirCardapio(){
        System.out.println("\n=== CARDÁPIO ===");

        for(Produto produto : produtos){
            produto.exibirDetalhes();
            System.out.println("---------------------------");
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
            System.out.println("O produto " + produtoEncontrado.getNome() + " foi removido!");
        }else{
            System.out.println("Erro: Produto com ID " + id + " não encontrado!");
        }
    }
}
