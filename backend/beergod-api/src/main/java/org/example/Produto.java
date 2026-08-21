package org.example;

public class Produto {
    private Long id;
    private String nome;
    private String descricao;
    private double  preco;

    public Produto(Long id, String nome, String descricao, double preco){
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }

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
}
