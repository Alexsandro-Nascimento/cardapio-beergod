package org.example.infrastructure.security;

import org.example.application.security.PasswordHasher;
import org.example.domain.model.Administrador;
import org.example.domain.repository.AdministradorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminPasswordMigrationRunner implements CommandLineRunner {

    private final AdministradorRepository repository;
    private final PasswordHasher passwordHasher;

    public AdminPasswordMigrationRunner(AdministradorRepository repository,
                                        PasswordHasher passwordHasher) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void run(String... args) {
        for (Administrador admin : repository.listarTodos()) {
            if (!passwordHasher.isHashed(admin.getSenha())) {
                String hash = passwordHasher.hash(admin.getSenha());
                admin.atualizar(admin.getNome(), admin.getEmail(), hash, admin.isAtivo());
                repository.atualizar(admin);
            }
        }
    }
}