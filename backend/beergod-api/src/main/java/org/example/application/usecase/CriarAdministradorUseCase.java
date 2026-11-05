package org.example.application.usecase;

import org.example.application.dto.CriarAdministradorDTO;
import org.example.domain.model.Administrador;
import org.example.domain.repository.AdministradorRepository;

public class CriarAdministradorUseCase {
    private final AdministradorRepository administradorRepository;

    public CriarAdministradorUseCase(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    public Administrador criarAdministrador(CriarAdministradorDTO dto) {
        if (administradorRepository.buscarPorEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Já existe um administrador cadastrado com este e-mail.");
        }

        Administrador novoAdmin = new Administrador(dto.nome(), dto.email(), dto.senha());
        return administradorRepository.salvar(novoAdmin);
    }
}
