package org.example.application.usecase;

import org.example.application.dto.CriarAdministradorDTO;
import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Administrador;
import org.example.domain.repository.AdministradorRepository;

public class CriarAdministradorUseCase {

    private final AdministradorRepository repository;

    public CriarAdministradorUseCase(AdministradorRepository repository) {
        this.repository = repository;
    }

    public Administrador criarAdministrador(CriarAdministradorDTO dto) {
        if (dto.email() != null && repository.buscarPorEmail(dto.email().trim()).isPresent()) {
            throw new RegraNegocioException("Já existe um administrador cadastrado com este e-mail!");
        }

        Administrador novo = new Administrador(dto.nome(), dto.email(), dto.senha());
        repository.salvar(novo);

        return repository.buscarPorEmail(novo.getEmail()).orElse(novo);
    }
}