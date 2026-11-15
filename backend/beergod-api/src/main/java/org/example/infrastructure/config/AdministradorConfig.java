package org.example.infrastructure.config;

import org.example.application.security.PasswordHasher;
import org.example.application.security.TokenService;
import org.example.application.usecase.*;
import org.example.domain.repository.AdministradorRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdministradorConfig {

    @Bean
    public CriarAdministradorUseCase criarAdministradorUseCase(AdministradorRepository repository,
                                                               PasswordHasher passwordHasher) {
        return new CriarAdministradorUseCase(repository, passwordHasher);
    }

    @Bean
    public ListarAdministradoresUseCase listarAdministradoresUseCase(AdministradorRepository repository) {
        return new ListarAdministradoresUseCase(repository);
    }

    @Bean
    public LoginAdministradorUseCase loginAdministradorUseCase(AdministradorRepository repository,
                                                               PasswordHasher passwordHasher,
                                                               TokenService tokenService) {
        return new LoginAdministradorUseCase(repository, passwordHasher, tokenService);
    }

    @Bean
    public AtualizarAdministradorUseCase atualizarAdministradorUseCase(AdministradorRepository repository,
                                                                       PasswordHasher passwordHasher) {
        return new AtualizarAdministradorUseCase(repository, passwordHasher);
    }

    @Bean
    public InativarAdministradorUseCase inativarAdministradorUseCase(AdministradorRepository repository) {
        return new InativarAdministradorUseCase(repository);
    }
}