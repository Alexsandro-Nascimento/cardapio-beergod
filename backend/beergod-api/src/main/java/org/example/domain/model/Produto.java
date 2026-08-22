package org.example.domain.model;

// CLASSE E ATRIBUTOS
public class Produto {
    private Long id;
    private String nome;
    private String descricao;
    private double  preco;
    private CategoriaProduto categoria;
    private boolean ativo;

    // CONSTRUTOR
    public Produto(Long id, String nome, String descricao, double preco, CategoriaProduto categoria){
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.ativo = true;
        this.categoria = categoria;
    }

    // GETTERS E SETTERS
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public String getNome(){
        return nome;
    }
    public void setNome(String nome){
        this.nome = nome;
    }

    public String getDescricao(){
        return descricao;
    }
    public void setDescricao(String descricao){
        this.descricao = descricao;
    }

    public double getPreco(){
        return preco;
    }
    public void setPreco(double preco){
        if (preco > 0){
            this.preco = preco;
        }else{
            IO.println("Erro: o preço do produto não pode ser negativo!");
        }
    }

    public CategoriaProduto getCategoria(){
        return categoria;
    }
    public void setCategoria(CategoriaProduto categoria){
        this.categoria = categoria;
    }

    public boolean isAtivo(){
        return ativo;
    }
    public void setAtivo(boolean ativo){
        this.ativo = ativo;
    }

    // MÉTODOS
    public void exibirDetalhes(){
        IO.println("--- Resumo do Produto ---");
        IO.println("Nome: "+ this.nome);
        IO.println("Preço: "+ this.preco);
        IO.println("Categoria: "+ this.categoria);
    }
}
