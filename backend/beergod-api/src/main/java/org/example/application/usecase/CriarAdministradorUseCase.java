package org.example.application.usecase;

import org.example.application.dto.CriarAdministradorDTO;
import org.example.application.security.PasswordHasher;
import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Administrador;
import org.example.domain.model.PerfilAdministrador;
import org.example.domain.repository.AdministradorRepository;

public class CriarAdministradorUseCase {

    private final AdministradorRepository repository;
    private final PasswordHasher passwordHasher;

    public CriarAdministradorUseCase(AdministradorRepository repository, PasswordHasher passwordHasher) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
    }

    public Administrador criarAdministrador(CriarAdministradorDTO dto) {
        if (dto.email() != null && repository.buscarPorEmail(dto.email().trim()).isPresent()) {
            throw new RegraNegocioException("Já existe um administrador cadastrado com este e-mail!");
        }

        PerfilAdministrador perfil = PerfilAdministrador.fromString(dto.perfil());
        new Administrador(dto.nome(), dto.email(), dto.senha(), perfil);

        String senhaCriptografada = passwordHasher.hash(dto.senha());
        Administrador novo = new Administrador(dto.nome(), dto.email(), senhaCriptografada, perfil);
        repository.salvar(novo);

        return repository.buscarPorEmail(novo.getEmail()).orElse(novo);
    }
}