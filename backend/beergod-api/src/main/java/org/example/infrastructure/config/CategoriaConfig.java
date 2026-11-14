package org.example.infrastructure.config;

import org.example.application.usecase.AtualizarCategoriaUseCase;
import org.example.application.usecase.CriarCategoriaUseCase;
import org.example.application.usecase.InativarCategoriaUseCase;
import org.example.application.usecase.ListarCategoriasUseCase;
import org.example.domain.repository.CategoriaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoriaConfig {

    @Bean
    public CriarCategoriaUseCase criarCategoriaUseCase(CategoriaRepository repository) {
        return new CriarCategoriaUseCase(repository);
    }

    @Bean
    public ListarCategoriasUseCase listarCategoriasUseCase(CategoriaRepository repository) {
        return new ListarCategoriasUseCase(repository);
    }

    @Bean
    public AtualizarCategoriaUseCase atualizarCategoriaUseCase(CategoriaRepository repository) {
        return new AtualizarCategoriaUseCase(repository);
    }

    @Bean
    public InativarCategoriaUseCase inativarCategoriaUseCase(CategoriaRepository repository) {
        return new InativarCategoriaUseCase(repository);
    }
}