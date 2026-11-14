package org.example.application.usecase;

import org.example.application.dto.CategoriaRequestDTO;
import org.example.application.dto.CategoriaResponseDTO;
import org.example.domain.exception.RegraNegocioException;
import org.example.domain.model.Categoria;
import org.example.domain.repository.CategoriaRepository;

public class AtualizarCategoriaUseCase {

    private final CategoriaRepository repository;

    public AtualizarCategoriaUseCase(CategoriaRepository repository) {
        this.repository = repository;
    }

    public CategoriaResponseDTO executar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = repository.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Categoria não encontrada!"));

        categoria.atualizar(dto.nome(), dto.ativo());
        repository.atualizar(categoria);

        return new CategoriaResponseDTO(categoria.getId(), categoria.getNome(), categoria.isAtivo());
    }
}