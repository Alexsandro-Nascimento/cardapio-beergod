package org.example.domain.model;

import org.example.domain.exception.RegraNegocioException;

public class Categoria {

    private Long id;
    private String nome;
    private boolean ativo;

    public Categoria(String nome) {
        validarNome(nome);
        this.nome = nome.trim();
        this.ativo = true;
    }

    public Categoria(Long id, String nome, boolean ativo) {
        validarNome(nome);
        this.id = id;
        this.nome = nome.trim();
        this.ativo = ativo;
    }

    public void atualizar(String nome, Boolean ativo) {
        validarNome(nome);
        this.nome = nome.trim();
        if (ativo != null) {
            this.ativo = ativo;
        }
    }

    public void inativar() {
        this.ativo = false;
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("O nome da categoria é obrigatório!");
        }
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public boolean isAtivo() { return ativo; }
}