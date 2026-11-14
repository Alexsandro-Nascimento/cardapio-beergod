package org.example.infrastructure.controller;

import org.example.application.dto.ProdutoRequestDTO;
import org.example.application.dto.ProdutoResponseDTO;
import org.example.application.usecase.AtualizarProdutoUseCase;
import org.example.application.usecase.CriarProdutoUseCase;
import org.example.application.usecase.InativarProdutoUseCase;
import org.example.application.usecase.ListarProdutosUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final CriarProdutoUseCase criarProdutoUseCase;
    private final ListarProdutosUseCase listarProdutosUseCase;
    private final AtualizarProdutoUseCase atualizarProdutoUseCase;
    private final InativarProdutoUseCase inativarProdutoUseCase;

    public ProdutoController(CriarProdutoUseCase criarProdutoUseCase,
                             ListarProdutosUseCase listarProdutosUseCase,
                             AtualizarProdutoUseCase atualizarProdutoUseCase,
                             InativarProdutoUseCase inativarProdutoUseCase) {
        this.criarProdutoUseCase = criarProdutoUseCase;
        this.listarProdutosUseCase = listarProdutosUseCase;
        this.atualizarProdutoUseCase = atualizarProdutoUseCase;
        this.inativarProdutoUseCase = inativarProdutoUseCase;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(@RequestBody ProdutoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(criarProdutoUseCase.executar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listar(
            @RequestParam(defaultValue = "false") boolean apenasAtivos) {
        return ResponseEntity.ok(listarProdutosUseCase.executar(apenasAtivos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody ProdutoRequestDTO dto) {
        return ResponseEntity.ok(atualizarProdutoUseCase.executar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        inativarProdutoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}