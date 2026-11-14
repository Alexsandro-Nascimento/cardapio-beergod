package org.example.application.usecase;

import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Administrador;
import org.example.domain.repository.AdministradorRepository;

public class InativarAdministradorUseCase {

    private final AdministradorRepository repository;

    public InativarAdministradorUseCase(AdministradorRepository repository) {
        this.repository = repository;
    }

    public void executar(Long id) {
        Administrador admin = repository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Administrador não encontrado!"));

        admin.inativar();
        repository.atualizar(admin);
    }
}