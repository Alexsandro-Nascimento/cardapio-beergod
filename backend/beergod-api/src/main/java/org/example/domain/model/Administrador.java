package org.example.domain.model;

import org.example.domain.exception.RegraNegocioException;

public class Administrador {
    private long id;
    private String nome;
    private String email;
    private String senha;
    private boolean ativo;


    public Administrador(long id, String nome, String email, String senha, boolean ativo) {
        validarNome(nome);
        validarEmail(email);
        validarSenha(senha);

        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.ativo = ativo;
    }


    public Administrador(String nome, String email, String senha) {
        validarNome(nome);
        validarEmail(email);
        validarSenha(senha);

        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.ativo = true;
    }



    public void alterarNome(String novoNome) {
        validarNome(novoNome);
        this.nome = novoNome;
    }

    public void alterarEmail(String novoEmail) {
        validarEmail(novoEmail);
        this.email = novoEmail;
    }

    public void redefinirSenha(String novaSenha) {
        validarSenha(novaSenha);
        this.senha = novaSenha;
    }

    public void inativar() {
        this.ativo = false;
    }

    public void ativar() {
        this.ativo = true;
    }


    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O nome do administrador é obrigatório!");
        }
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new RegraNegocioException("O e-mail fornecido é obrigatório e deve ser válido!");
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.trim().length() < 6) {
            throw new RegraNegocioException("A senha deve conter no mínimo 6 caracteres!");
        }
    }


    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public boolean isAtivo() { return ativo; }
}