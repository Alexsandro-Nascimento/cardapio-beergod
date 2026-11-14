package org.example.domain.model;

import org.example.domain.exception.RegraNegocioException;
import java.math.BigDecimal;

public class Produto {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imagemUrl;
    private boolean ativo;
    private Long categoriaId;

    public Produto(String nome, String descricao, BigDecimal preco, String imagemUrl, Long categoriaId) {
        validar(nome, preco, categoriaId);
        this.nome = nome.trim();
        this.descricao = descricao != null ? descricao.trim() : null;
        this.preco = preco;
        this.imagemUrl = imagemUrl != null ? imagemUrl.trim() : null;
        this.ativo = true;
        this.categoriaId = categoriaId;
    }

    public Produto(Long id, String nome, String descricao, BigDecimal preco, String imagemUrl, boolean ativo, Long categoriaId) {
        validar(nome, preco, categoriaId);
        this.id = id;
        this.nome = nome.trim();
        this.descricao = descricao != null ? descricao.trim() : null;
        this.preco = preco;
        this.imagemUrl = imagemUrl != null ? imagemUrl.trim() : null;
        this.ativo = ativo;
        this.categoriaId = categoriaId;
    }

    public void atualizar(String nome, String descricao, BigDecimal preco, String imagemUrl, Boolean ativo, Long categoriaId) {
        validar(nome, preco, categoriaId);
        this.nome = nome.trim();
        this.descricao = descricao != null ? descricao.trim() : null;
        this.preco = preco;
        this.imagemUrl = imagemUrl != null ? imagemUrl.trim() : null;
        if (ativo != null) {
            this.ativo = ativo;
        }
        this.categoriaId = categoriaId;
    }

    public void inativar() {
        this.ativo = false;
    }

    private void validar(String nome, BigDecimal preco, Long categoriaId) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("O nome do produto é obrigatório!");
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O preço do produto deve ser maior que zero!");
        }
        if (categoriaId == null) {
            throw new RegraNegocioException("A categoria do produto é obrigatória!");
        }
    }

    public void exibirDetalhes() {
        System.out.println("--- Resumo do Produto ---");
        System.out.println("Nome: " + this.nome);
        System.out.println("Preço: " + this.preco);
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public BigDecimal getPreco() { return preco; }
    public String getImagemUrl() { return imagemUrl; }
    public boolean isAtivo() { return ativo; }
    public Long getCategoriaId() { return categoriaId; }
}