package org.example.infrastructure.controller;

import org.example.application.dto.CategoriaRequestDTO;
import org.example.application.dto.CategoriaResponseDTO;
import org.example.application.usecase.AtualizarCategoriaUseCase;
import org.example.application.usecase.CriarCategoriaUseCase;
import org.example.application.usecase.InativarCategoriaUseCase;
import org.example.application.usecase.ListarCategoriasUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CriarCategoriaUseCase criarCategoriaUseCase;
    private final ListarCategoriasUseCase listarCategoriasUseCase;
    private final AtualizarCategoriaUseCase atualizarCategoriaUseCase;
    private final InativarCategoriaUseCase inativarCategoriaUseCase;

    public CategoriaController(CriarCategoriaUseCase criarCategoriaUseCase,
                               ListarCategoriasUseCase listarCategoriasUseCase,
                               AtualizarCategoriaUseCase atualizarCategoriaUseCase,
                               InativarCategoriaUseCase inativarCategoriaUseCase) {
        this.criarCategoriaUseCase = criarCategoriaUseCase;
        this.listarCategoriasUseCase = listarCategoriasUseCase;
        this.atualizarCategoriaUseCase = atualizarCategoriaUseCase;
        this.inativarCategoriaUseCase = inativarCategoriaUseCase;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criar(@RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(criarCategoriaUseCase.executar(dto));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listar(
            @RequestParam(defaultValue = "false") boolean apenasAtivas) {
        return ResponseEntity.ok(listarCategoriasUseCase.executar(apenasAtivas));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizar(@PathVariable Long id,
                                                          @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.ok(atualizarCategoriaUseCase.executar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        inativarCategoriaUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}