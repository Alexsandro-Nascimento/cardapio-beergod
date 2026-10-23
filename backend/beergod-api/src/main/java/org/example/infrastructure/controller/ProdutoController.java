package org.example.infrastructure.controller;

import org.example.application.dto.CriarProdutoDTO;
import org.example.application.dto.ProdutoResponseDTO;
import org.example.application.usecase.BuscarProdutoPorIdUseCase;
import org.example.application.usecase.CadastrarProdutoUseCase;
import org.example.application.usecase.DeletarProdutoUseCase;
import org.example.application.usecase.ListarProdutosUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final CadastrarProdutoUseCase cadastrarProdutoUseCase;
    private final BuscarProdutoPorIdUseCase buscarProdutoPorIdUseCase;
    private final ListarProdutosUseCase listarProdutosUseCase;
    private final DeletarProdutoUseCase deletarProdutoUseCase;

    public ProdutoController(CadastrarProdutoUseCase cadastrarProdutoUseCase, BuscarProdutoPorIdUseCase buscarProdutoPorIdUseCase, ListarProdutosUseCase listarProdutosUseCase, DeletarProdutoUseCase deletarProdutoUseCase) {
        this.cadastrarProdutoUseCase = cadastrarProdutoUseCase;
        this.buscarProdutoPorIdUseCase = buscarProdutoPorIdUseCase;
        this.listarProdutosUseCase = listarProdutosUseCase;
        this.deletarProdutoUseCase = deletarProdutoUseCase;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@RequestBody CriarProdutoDTO dto){
        ProdutoResponseDTO response = cadastrarProdutoUseCase.executar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listar() {
        List<ProdutoResponseDTO> produtos = listarProdutosUseCase.executar();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        ProdutoResponseDTO response = buscarProdutoPorIdUseCase.executar(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        deletarProdutoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}
