package org.example.infrastructure.controller;

import org.example.application.dto.*;
import org.example.application.usecase.*;
import org.example.domain.model.Administrador;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/administradores")
public class AdministradorController {

    private final CriarAdministradorUseCase criarAdministradorUseCase;
    private final ListarAdministradoresUseCase listarAdministradoresUseCase;
    private final LoginAdministradorUseCase loginAdministradorUseCase;
    private final AtualizarAdministradorUseCase atualizarAdministradorUseCase;
    private final InativarAdministradorUseCase inativarAdministradorUseCase;

    public AdministradorController(CriarAdministradorUseCase criarAdministradorUseCase,
                                   ListarAdministradoresUseCase listarAdministradoresUseCase,
                                   LoginAdministradorUseCase loginAdministradorUseCase,
                                   AtualizarAdministradorUseCase atualizarAdministradorUseCase,
                                   InativarAdministradorUseCase inativarAdministradorUseCase) {
        this.criarAdministradorUseCase = criarAdministradorUseCase;
        this.listarAdministradoresUseCase = listarAdministradoresUseCase;
        this.loginAdministradorUseCase = loginAdministradorUseCase;
        this.atualizarAdministradorUseCase = atualizarAdministradorUseCase;
        this.inativarAdministradorUseCase = inativarAdministradorUseCase;
    }

    @PostMapping
    public ResponseEntity<AdministradorResponseDTO> criar(@RequestBody CriarAdministradorDTO dto) {
        Administrador adminCriado = criarAdministradorUseCase.criarAdministrador(dto);
        AdministradorResponseDTO response = new AdministradorResponseDTO(
                adminCriado.getId(),
                adminCriado.getNome(),
                adminCriado.getEmail(),
                adminCriado.isAtivo(),
                adminCriado.getPerfil().name()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(loginAdministradorUseCase.executar(dto));
    }

    @GetMapping
    public ResponseEntity<List<AdministradorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(listarAdministradoresUseCase.executar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministradorResponseDTO> atualizar(@PathVariable Long id,
                                                              @RequestBody AtualizarAdministradorDTO dto) {
        return ResponseEntity.ok(atualizarAdministradorUseCase.executar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        inativarAdministradorUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}