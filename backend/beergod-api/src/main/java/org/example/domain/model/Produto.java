package org.example.domain.model;
import org.example.domain.exception.RegraNegocioException;


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
        validar(nome, preco);

        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.ativo = true;
        this.categoria = categoria;
    }

    // MÉTODO PRIVADO DE VALIDAÇÃO
    private void validar(String nome, double preco){
        if(nome == null || nome.isBlank()){
            throw new RegraNegocioException("O nome do produto é obrigatório!");
        }

        if(preco <= 0){
            throw new RegraNegocioException("O valor do produto precisa ser maior que zero!");
        }
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
        if(nome == null || nome.isBlank()){
            throw new RegraNegocioException("O nome do produto é obrigatório!");
        }
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
        if (preco <= 0) {
            throw new RegraNegocioException("O preço do produto deve ser maior que zero.");
        }
        this.preco = preco;
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
