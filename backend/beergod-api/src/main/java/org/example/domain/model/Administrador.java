package org.example.domain.model;

import org.example.domain.exception.RegraNegocioException;

public class Administrador {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private boolean ativo;
    private PerfilAdministrador perfil;

    public Administrador(String nome, String email, String senha) {
        this(nome, email, senha, PerfilAdministrador.ADMIN_MASTER);
    }

    public Administrador(String nome, String email, String senha, PerfilAdministrador perfil) {
        validarDados(nome, email, senha, true);
        this.nome = nome.trim();
        this.email = email.trim();
        this.senha = senha;
        this.ativo = true;
        this.perfil = perfil != null ? perfil : PerfilAdministrador.ADMIN_MASTER;
    }

    public Administrador(Long id, String nome, String email, String senha, boolean ativo) {
        this(id, nome, email, senha, ativo, PerfilAdministrador.ADMIN_MASTER);
    }

    public Administrador(Long id, String nome, String email, String senha, boolean ativo, PerfilAdministrador perfil) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.ativo = ativo;
        this.perfil = perfil != null ? perfil : PerfilAdministrador.ADMIN_MASTER;
    }

    public void atualizar(String nome, String email, String senha, Boolean ativo) {
        atualizar(nome, email, senha, ativo, null);
    }

    public void atualizar(String nome, String email, String senha, Boolean ativo, PerfilAdministrador perfil) {
        boolean validarSenha = (senha != null && !senha.trim().isEmpty());
        validarDados(nome, email, senha, validarSenha);

        this.nome = nome.trim();
        this.email = email.trim();

        if (validarSenha) {
            this.senha = senha;
        }
        if (ativo != null) {
            this.ativo = ativo;
        }
        if (perfil != null) {
            this.perfil = perfil;
        }
    }

    public void inativar() {
        this.ativo = false;
    }

    private void validarDados(String nome, String email, String senha, boolean validarSenha) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("O nome do administrador é obrigatório!");
        }
        if (email == null || !email.contains("@")) {
            throw new RegraNegocioException("O e-mail fornecido é obrigatório e deve ser válido!");
        }
        if (validarSenha && (senha == null || senha.length() < 6)) {
            throw new RegraNegocioException("A senha deve ter no mínimo 6 caracteres!");
        }
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public boolean isAtivo() { return ativo; }
    public PerfilAdministrador getPerfil() { return perfil; }
}