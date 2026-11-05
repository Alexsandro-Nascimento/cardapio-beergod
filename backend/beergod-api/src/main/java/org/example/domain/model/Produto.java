package org.example.domain.model;
import org.example.domain.exception.RegraNegocioException;

public class Produto {
    private Long id;
    private String nome;
    private String descricao;
    private double preco;
    private CategoriaProduto categoria;
    private boolean ativo;

    // CONSTRUTOR
    public Produto(Long id, String nome, String descricao, double preco, CategoriaProduto categoria){
        validarNome(nome);
        validarPreco(preco);

        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
        this.ativo = true;
    }


    public void alterarNome(String novoNome) {
        validarNome(novoNome);
        this.nome = novoNome;
    }

    public void alterarPreco(double novoPreco) {
        validarPreco(novoPreco);
        this.preco = novoPreco;
    }

    public void alterarDescricao(String novaDescricao) {
        this.descricao = novaDescricao;
    }

    public void alterarCategoria(CategoriaProduto novaCategoria) {
        this.categoria = novaCategoria;
    }

    public void inativar() {
        this.ativo = false;
    }

    public void ativar() {
        this.ativo = true;
    }


    private void validarNome(String nome) {
        if(nome == null || nome.isBlank()){
            throw new RegraNegocioException("O nome do produto é obrigatório!");
        }
    }

    private void validarPreco(double preco) {
        if(preco <= 0){
            throw new RegraNegocioException("O valor do produto precisa ser maior que zero!");
        }
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }
    public CategoriaProduto getCategoria() { return categoria; }
    public boolean isAtivo() { return ativo; }


    public void exibirDetalhes(){
        System.out.println("--- Resumo do Produto ---");
        System.out.println("Nome: "+ this.nome);
        System.out.println("Preço: "+ this.preco);
        System.out.println("Categoria: "+ this.categoria);
    }
}