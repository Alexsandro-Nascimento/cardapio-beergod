package org.example.infrastructure.config;

import org.example.application.usecase.CriarAdministradorUseCase;
import org.example.domain.repository.AdministradorRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdministradorConfig {
    @Bean
    public CriarAdministradorUseCase criarAdministradorUseCase(AdministradorRepository administradorRepository) {
        return new CriarAdministradorUseCase(administradorRepository);
    }
}
