package org.example.application.usecase;

import org.example.application.dto.CategoriaResponseDTO;
import org.example.domain.repository.CategoriaRepository;
import java.util.List;

public class ListarCategoriasUseCase {

    private final CategoriaRepository repository;

    public ListarCategoriasUseCase(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<CategoriaResponseDTO> executar(boolean apenasAtivas) {
        return repository.listarTodos(apenasAtivas).stream()
                .map(c -> new CategoriaResponseDTO(c.getId(), c.getNome(), c.isAtivo()))
                .toList();
    }
}