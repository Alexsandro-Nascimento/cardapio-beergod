package org.example;

public class Bebida extends Produto{
    private int tamanhoML;
    private String marca;

    public Bebida(Long id, String nome, String descricao, double preco, int tamanhoML, String marca){

        super(id, nome, descricao, preco, CategoriaProduto.BEBIDAS);

        this.tamanhoML = tamanhoML;
        this.marca = marca;
    }

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
}
