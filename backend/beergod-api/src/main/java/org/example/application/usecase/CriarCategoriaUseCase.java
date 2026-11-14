package org.example.application.usecase;

import org.example.application.dto.CategoriaRequestDTO;
import org.example.application.dto.CategoriaResponseDTO;
import org.example.domain.model.Categoria;
import org.example.domain.repository.CategoriaRepository;

public class CriarCategoriaUseCase {

    private final CategoriaRepository repository;

    public CriarCategoriaUseCase(CategoriaRepository repository) {
        this.repository = repository;
    }

    public CategoriaResponseDTO executar(CategoriaRequestDTO dto) {
        Categoria nova = new Categoria(dto.nome());
        Categoria salva = repository.salvar(nova);
        return new CategoriaResponseDTO(salva.getId(), salva.getNome(), salva.isAtivo());
    }
}