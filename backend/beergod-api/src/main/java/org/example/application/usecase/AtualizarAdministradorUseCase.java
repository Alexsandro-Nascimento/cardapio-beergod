package org.example.application.usecase;

import org.example.application.dto.AdministradorResponseDTO;
import org.example.application.dto.AtualizarAdministradorDTO;
import org.example.application.security.PasswordHasher;
import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Administrador;
import org.example.domain.repository.AdministradorRepository;

public class AtualizarAdministradorUseCase {

    private final AdministradorRepository repository;
    private final PasswordHasher passwordHasher;

    public AtualizarAdministradorUseCase(AdministradorRepository repository, PasswordHasher passwordHasher) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
    }

    public AdministradorResponseDTO executar(Long id, AtualizarAdministradorDTO dto) {
        Administrador admin = repository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Administrador não encontrado!"));

        // Valida os dados e a nova senha em texto puro (caso informada)
        admin.atualizar(dto.nome(), dto.email(), dto.senha(), dto.ativo());

        if (dto.senha() != null && !dto.senha().trim().isEmpty()) {
            String senhaCriptografada = passwordHasher.hash(dto.senha());
            admin.atualizar(dto.nome(), dto.email(), senhaCriptografada, dto.ativo());
        }

        repository.atualizar(admin);

        return new AdministradorResponseDTO(
                admin.getId(),
                admin.getNome(),
                admin.getEmail(),
                admin.isAtivo()
        );
    }
}