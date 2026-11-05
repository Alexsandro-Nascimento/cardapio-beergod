package org.example.infrastructure.config;

import org.example.application.usecase.CriarAdministradorUseCase;
import org.example.application.usecase.ListarAdministradoresUseCase;
import org.example.domain.repository.AdministradorRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdministradorConfig {
    @Bean
    public CriarAdministradorUseCase criarAdministradorUseCase(AdministradorRepository administradorRepository) {
        return new CriarAdministradorUseCase(administradorRepository);
    }
    @Bean
    public ListarAdministradoresUseCase listarAdministradoresUseCase(AdministradorRepository administradorRepository) {
        return new ListarAdministradoresUseCase(administradorRepository);
    }
}
