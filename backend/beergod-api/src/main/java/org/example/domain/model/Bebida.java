package org.example.domain.model;
// CLASSE E ATRIBUTOS
public class Bebida extends Produto{
    private int tamanhoML;
    private String marca;

    //CONSTRUTOR
    public Bebida(Long id, String nome, String descricao, double preco, int tamanhoML, String marca){

        super(id, nome, descricao, preco, CategoriaProduto.BEBIDAS);

        this.tamanhoML = tamanhoML;
        this.marca = marca;
    }

    // GETTERS E SETTERS
    public int getTamanhoML(){
        return tamanhoML;
    }
    public void setTamanhoML(int tamanhoML){
        this.tamanhoML = tamanhoML;
    }

    public String getMarca(){
        return marca;
    }
    public void setMarca(String marca){
        this.marca = marca;
    }

    // MÉTODOS
    @Override
    public void exibirDetalhes(){
        IO.println("--- Resumo do Produto ---");
        IO.println("Nome: "+ getNome());
        IO.println("Preço: "+ getPreco());
        IO.println("Tamanho: "+ this.tamanhoML);
        IO.println("Marca: "+ this.marca);
    }
}
