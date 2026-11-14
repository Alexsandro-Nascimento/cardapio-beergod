package org.example.infrastructure.config;

import org.example.application.usecase.*;
import org.example.domain.repository.AdministradorRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdministradorConfig {

    @Bean
    public CriarAdministradorUseCase criarAdministradorUseCase(AdministradorRepository repository) {
        return new CriarAdministradorUseCase(repository);
    }

    @Bean
    public ListarAdministradoresUseCase listarAdministradoresUseCase(AdministradorRepository repository) {
        return new ListarAdministradoresUseCase(repository);
    }

    @Bean
    public LoginAdministradorUseCase loginAdministradorUseCase(AdministradorRepository repository) {
        return new LoginAdministradorUseCase(repository);
    }

    @Bean
    public AtualizarAdministradorUseCase atualizarAdministradorUseCase(AdministradorRepository repository) {
        return new AtualizarAdministradorUseCase(repository);
    }

    @Bean
    public InativarAdministradorUseCase inativarAdministradorUseCase(AdministradorRepository repository) {
        return new InativarAdministradorUseCase(repository);
    }
}