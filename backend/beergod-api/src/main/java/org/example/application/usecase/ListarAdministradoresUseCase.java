package org.example.application.usecase;

import org.example.application.dto.AdministradorResponseDTO;
import org.example.domain.repository.AdministradorRepository;

import java.util.List;

public class ListarAdministradoresUseCase {

    private final AdministradorRepository repository;

    public ListarAdministradoresUseCase(AdministradorRepository repository) {
        this.repository = repository;
    }

    public List<AdministradorResponseDTO> executar() {
        return repository.listarTodos().stream()
                .map(admin -> new AdministradorResponseDTO(
                        admin.getId(),
                        admin.getNome(),
                        admin.getEmail(),
                        admin.isAtivo()
                ))
                .toList();
    }
}