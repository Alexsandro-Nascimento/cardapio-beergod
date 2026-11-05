package org.example.domain.repository;
import org.example.domain.model.Administrador;
import java.util.List;
import java.util.Optional;

public interface AdministradorRepository {
    Administrador salvar(Administrador administrador);
    Optional<Administrador> buscarPorEmail(String email);
    List<Administrador> listarTodos();
}
