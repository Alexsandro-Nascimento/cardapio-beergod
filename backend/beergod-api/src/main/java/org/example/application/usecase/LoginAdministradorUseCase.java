package org.example.application.usecase;

import org.example.application.dto.AdministradorResponseDTO;
import org.example.application.dto.LoginRequestDTO;
import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Administrador;
import org.example.domain.repository.AdministradorRepository;

public class LoginAdministradorUseCase {

    private final AdministradorRepository repository;

    public LoginAdministradorUseCase(AdministradorRepository repository) {
        this.repository = repository;
    }

    public AdministradorResponseDTO executar(LoginRequestDTO dto) {
        if (dto.email() == null || dto.senha() == null) {
            throw new RegraNegocioException("E-mail e senha são obrigatórios!");
        }

        Administrador admin = repository.buscarPorEmail(dto.email().trim())
                .orElseThrow(() -> new RegraNegocioException("E-mail ou senha inválidos!"));

        if (!admin.isAtivo()) {
            throw new RegraNegocioException("Este administrador está inativo!");
        }

        if (!admin.getSenha().equals(dto.senha())) {
            throw new RegraNegocioException("E-mail ou senha inválidos!");
        }

        return new AdministradorResponseDTO(
                admin.getId(),
                admin.getNome(),
                admin.getEmail(),
                admin.isAtivo()
        );
    }
}