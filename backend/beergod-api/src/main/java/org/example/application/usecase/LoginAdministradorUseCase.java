package org.example.application.usecase;

import org.example.application.dto.LoginRequestDTO;
import org.example.application.dto.LoginResponseDTO;
import org.example.application.security.PasswordHasher;
import org.example.application.security.TokenService;
import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Administrador;
import org.example.domain.repository.AdministradorRepository;

public class LoginAdministradorUseCase {

    private final AdministradorRepository repository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;

    public LoginAdministradorUseCase(AdministradorRepository repository,
                                     PasswordHasher passwordHasher,
                                     TokenService tokenService) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
    }

    public LoginResponseDTO executar(LoginRequestDTO dto) {
        if (dto.email() == null || dto.email().trim().isEmpty()
                || dto.senha() == null || dto.senha().isEmpty()) {
            throw new RegraNegocioException("E-mail e senha são obrigatórios!");
        }

        Administrador admin = repository.buscarPorEmail(dto.email().trim())
                .orElseThrow(() -> new RegraNegocioException("E-mail ou senha inválidos!"));

        if (!admin.isAtivo()) {
            throw new RegraNegocioException("Este administrador está inativo!");
        }

        boolean senhaValida;
        if (passwordHasher.isHashed(admin.getSenha())) {
            senhaValida = passwordHasher.matches(dto.senha(), admin.getSenha());
        } else {
            senhaValida = admin.getSenha().equals(dto.senha());
            if (senhaValida) {
                admin.atualizar(admin.getNome(), admin.getEmail(), passwordHasher.hash(dto.senha()), admin.isAtivo());
                repository.atualizar(admin);
            }
        }

        if (!senhaValida) {
            throw new RegraNegocioException("E-mail ou senha inválidos!");
        }

        String token = tokenService.gerarToken(admin);

        return new LoginResponseDTO(
                admin.getId(),
                admin.getNome(),
                admin.getEmail(),
                admin.isAtivo(),
                admin.getPerfil().name(),
                token
        );
    }
}