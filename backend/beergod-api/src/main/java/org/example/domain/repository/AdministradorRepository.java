package org.example.domain.repository;

import org.example.domain.model.Administrador;
import java.util.List;
import java.util.Optional;

public interface AdministradorRepository {
    void salvar(Administrador administrador);
    List<Administrador> listarTodos();
    Optional<Administrador> buscarPorEmail(String email);
    Optional<Administrador> buscarPorId(Long id);
    void atualizar(Administrador administrador);
}