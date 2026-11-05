package org.example.infrastructure.controller;

import org.example.application.dto.CriarAdministradorDTO;
import org.example.application.dto.AdministradorResponseDTO;
import org.example.application.usecase.CriarAdministradorUseCase;
import org.example.application.usecase.ListarAdministradoresUseCase;
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

    public AdministradorController(CriarAdministradorUseCase criarAdministradorUseCase,ListarAdministradoresUseCase listarAdministradoresUseCase) {
        this.criarAdministradorUseCase = criarAdministradorUseCase;
        this.listarAdministradoresUseCase = listarAdministradoresUseCase;
    }

    @PostMapping
    public ResponseEntity<AdministradorResponseDTO> criar(@RequestBody CriarAdministradorDTO dto) {
        Administrador adminCriado = criarAdministradorUseCase.criarAdministrador(dto);

        AdministradorResponseDTO response = new AdministradorResponseDTO(
                adminCriado.getId(),
                adminCriado.getNome(),
                adminCriado.getEmail(),
                adminCriado.isAtivo()
        );

    return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AdministradorResponseDTO>> listarTodos() {
        List<AdministradorResponseDTO> lista = listarAdministradoresUseCase.executar();
        return ResponseEntity.ok(lista); // Retorna Status 200 OK com o JSON da lista
    }
}

