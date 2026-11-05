package org.example.application.usecase;

import org.example.application.dto.AdministradorResponseDTO;
import org.example.domain.repository.AdministradorRepository;

import java.util.List;
import java.util.stream.Collectors;

public class ListarAdministradoresUseCase {
    private AdministradorRepository administradorRepository;
    public ListarAdministradoresUseCase(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    public List<AdministradorResponseDTO> executar() {
        // Busca as entidades do domínio e converte para DTOs de saída
        return administradorRepository.listarTodos().stream()
                .map(admin -> new AdministradorResponseDTO(
                        admin.getId(),
                        admin.getNome(),
                        admin.getEmail(),
                        admin.isAtivo()
                ))
                .collect(Collectors.toList());
    }
}
