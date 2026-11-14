package org.example.application.usecase;

import org.example.application.dto.AdministradorResponseDTO;
import org.example.application.dto.AtualizarAdministradorDTO;
import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Administrador;
import org.example.domain.repository.AdministradorRepository;

public class AtualizarAdministradorUseCase {

    private final AdministradorRepository repository;

    public AtualizarAdministradorUseCase(AdministradorRepository repository) {
        this.repository = repository;
    }

    public AdministradorResponseDTO executar(Long id, AtualizarAdministradorDTO dto) {
        Administrador admin = repository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Administrador não encontrado!"));

        admin.atualizar(dto.nome(), dto.email(), dto.senha(), dto.ativo());
        repository.atualizar(admin);

        return new AdministradorResponseDTO(
                admin.getId(),
                admin.getNome(),
                admin.getEmail(),
                admin.isAtivo()
        );
    }
}