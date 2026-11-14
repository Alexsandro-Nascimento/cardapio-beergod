package org.example.domain.repository;

import org.example.domain.model.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaRepository {
    Categoria salvar(Categoria categoria);
    void atualizar(Categoria categoria);
    Optional<Categoria> buscarPorId(Long id);
    List<Categoria> listarTodos(boolean apenasAtivas);
}