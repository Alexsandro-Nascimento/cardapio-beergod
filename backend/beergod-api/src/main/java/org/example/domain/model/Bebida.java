package org.example.domain.model;

import org.example.domain.exception.RegraNegocioException;
import java.math.BigDecimal;

public class Bebida extends Produto {

    private int tamanhoML;
    private String marca;

    public Bebida(String nome, String descricao, BigDecimal preco, String imagemUrl, Long categoriaId, int tamanhoML, String marca) {
        super(nome, descricao, preco, imagemUrl, categoriaId);
        validarBebida(tamanhoML, marca);
        this.tamanhoML = tamanhoML;
        this.marca = marca.trim();
    }

    public Bebida(Long id, String nome, String descricao, BigDecimal preco, String imagemUrl, boolean ativo, Long categoriaId, int tamanhoML, String marca) {
        super(id, nome, descricao, preco, imagemUrl, ativo, categoriaId);
        validarBebida(tamanhoML, marca);
        this.tamanhoML = tamanhoML;
        this.marca = marca.trim();
    }

    public void atualizarBebida(String nome, String descricao, BigDecimal preco, String imagemUrl, Boolean ativo, Long categoriaId, int tamanhoML, String marca) {
        super.atualizar(nome, descricao, preco, imagemUrl, ativo, categoriaId);
        validarBebida(tamanhoML, marca);
        this.tamanhoML = tamanhoML;
        this.marca = marca.trim();
    }

    private void validarBebida(int tamanhoML, String marca) {
        if (tamanhoML <= 0) {
            throw new RegraNegocioException("O tamanho da bebida deve ser maior que zero!");
        }
        if (marca == null || marca.isBlank()) {
            throw new RegraNegocioException("O nome da marca é obrigatório!");
        }
    }

    public int getTamanhoML() { return tamanhoML; }
    public String getMarca() { return marca; }

    @Override
    public void exibirDetalhes() {
        super.exibirDetalhes();
        System.out.println("Tamanho: " + this.tamanhoML + "ml");
        System.out.println("Marca: " + this.marca);
    }
}