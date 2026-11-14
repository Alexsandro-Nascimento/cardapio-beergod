package org.example.application.usecase;

import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Categoria;
import org.example.domain.repository.CategoriaRepository;

public class InativarCategoriaUseCase {

    private final CategoriaRepository repository;

    public InativarCategoriaUseCase(CategoriaRepository repository) {
        this.repository = repository;
    }

    public void executar(Long id) {
        Categoria categoria = repository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Categoria não encontrada!"));

        categoria.inativar();
        repository.atualizar(categoria);
    }
}