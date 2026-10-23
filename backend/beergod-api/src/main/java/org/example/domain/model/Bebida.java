package org.example.domain.model;

import org.example.domain.exception.RegraNegocioException;

// CLASSE E ATRIBUTOS
public class Bebida extends Produto{
    private int tamanhoML;
    private String marca;

    //CONSTRUTOR
    public Bebida(Long id, String nome, String descricao, double preco, int tamanhoML, String marca){

        super(id, nome, descricao, preco, CategoriaProduto.BEBIDAS);

        validarBebida(tamanhoML, marca);

        this.tamanhoML = tamanhoML;
        this.marca = marca;
    }
    // MÉTODO DE VALIDAÇÃO PRIVADO
    private void validarBebida(int tamanhoML, String marca){
        if(tamanhoML <= 0){
            throw new RegraNegocioException("O tamanho da bebida deve ser maior que zero!");
        }

        if(marca == null || marca.isBlank()){
            throw new RegraNegocioException("O nome da marca é obrigatório!");
        }
    }

    // GETTERS E SETTERS
    public int getTamanhoML(){
        return tamanhoML;
    }
    public void setTamanhoML(int tamanhoML){
        if(tamanhoML <= 0){
            throw new RegraNegocioException("O tamanho da bebida deve ser maior que zero!");
        }
        this.tamanhoML = tamanhoML;
    }

    public String getMarca(){
        return marca;
    }
    public void setMarca(String marca){
        if(marca == null || marca.isBlank()){
            throw new RegraNegocioException("O nome da marca é obrigatório!");
        }
        this.marca = marca;
    }

    // MÉTODOS
    @Override
    public void exibirDetalhes(){
        System.out.println("--- Resumo do Produto ---");
        System.out.println("Nome: "+ getNome());
        System.out.println("Preço: "+ getPreco());
        System.out.println("Tamanho: "+ this.tamanhoML);
        System.out.println("Marca: "+ this.marca);
    }
}
