package org.example.domain.repository;

import org.example.domain.model.Produto;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepository {
    Produto salvar(Produto produto);
    void atualizar(Produto produto);
    Optional<Produto> buscarPorId(Long id);
    List<Produto> listarTodos(boolean apenasAtivos);
}